package jadelab2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;

public class BookBuyerAgent extends Agent {
    private BookBuyerGui myGui;
    private String targetBookTitle;
    private int budget;

    //list of found sellers
    private AID[] sellerAgents;
    private int searchIterations = 0;
    private int maxSearchIterations = 10;

    protected void setup() {
        targetBookTitle = "";
        System.out.println("Hello! " + getAID().getLocalName() + " is ready for the purchase order.");
        myGui = new BookBuyerGui(this);
        myGui.display();
        //time interval for buyer for sending subsequent CFP
        //as a CLI argument
        int interval = 20000;
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            interval = Integer.parseInt(args[0].toString());
            this.budget = Integer.parseInt(args[1].toString());
        }
		addBehaviour(new TickerBehaviour(this, interval) {
            protected void onTick() {
                //search only if the purchase task was ordered
                searchIterations++;
                if (!targetBookTitle.equals("")) {
                    System.out.println(getAID().getLocalName() + ": I'm looking for " + targetBookTitle);
                    //update a list of known sellers (DF)
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("book-selling");
                    template.addServices(sd);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println(getAID().getLocalName() + ": the following sellers have been found");
                        sellerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; ++i) {
                            sellerAgents[i] = result[i].getName();
                            System.out.println(sellerAgents[i].getLocalName());
                        }
                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    myAgent.addBehaviour(new RequestPerformer());
                }
            }
        });
    }

    //invoked from GUI, when purchase was ordered
    public void lookForTitle(final String title) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                targetBookTitle = title;
                System.out.println(getAID().getLocalName() + ": purchase order for " + targetBookTitle + " accepted");
            }
        });
    }

    protected void takeDown() {
        myGui.dispose();
        System.out.println("Buyer agent " + getAID().getLocalName() + " terminated.");
    }

    private class RequestPerformer extends Behaviour {
        private AID bestSeller;
        private int bestPrice;
        private MessageTemplate mt;
        private int step = 0;

        public void action() {
            switch (step) {
                case 0:
                    //call for proposal (CFP) to found sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < sellerAgents.length; ++i) {
                        cfp.addReceiver(sellerAgents[i]);
                    }
                    cfp.setContent(targetBookTitle);
                    cfp.setConversationId("book-trade");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); //unique value
                    myAgent.send(cfp);
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    //collect proposals
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            //proposal received
                            int price = Integer.parseInt(reply.getContent());
                            if (bestSeller == null || price < bestPrice) {
                                //the best proposal as for now
                                bestPrice = price;
                                bestSeller = reply.getSender();
                            }
                        }

                        sellerAgents = Arrays.stream(sellerAgents)
                                .filter((sellerAgent) -> !sellerAgent.equals(reply.getSender()))
                                .toArray(AID[]::new);

                        if (searchIterations > maxSearchIterations && bestSeller != null) {
                            searchIterations = 0;
                            if (budget < bestPrice) {
                                System.out.println(getAID().getLocalName() + ": purchase has failed. Budget is too low. Budged is: " + budget + " , best price found: " + bestPrice);
                                targetBookTitle = "";
                                step = 4;
                            } else {
                                step = 2;
                            }
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    //best proposal consumption - purchase
                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    order.addReceiver(bestSeller);
                    order.setContent(targetBookTitle);
                    order.setConversationId("book-trade");
                    order.setReplyWith("order" + System.currentTimeMillis());
                    myAgent.send(order);
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    step = 3;
                    break;
                case 3:
                    //seller confirms the transaction
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            //purchase succeeded
                            budget -= bestPrice;
                            System.out.println(getAID().getLocalName() + ": " + targetBookTitle + " purchased for " + bestPrice + " from " + reply.getSender().getLocalName());
                            System.out.println(getAID().getLocalName() + ": waiting for the next purchase order. Budget left: " + budget);
                            targetBookTitle = "";
                            //myAgent.doDelete();
                        } else {
                            System.out.println(getAID().getLocalName() + ": purchase has failed. " + targetBookTitle + " was sold in the meantime.");
                        }
                        step = 4;    //this state ends the purchase process
                    } else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            if (step == 2 && bestSeller == null) {
                System.out.println(getAID().getLocalName() + ": " + targetBookTitle + " is not on sale.");
            }
            //process terminates here if purchase has failed (title not on sale) or book was successfully bought
            return ((step == 2 && bestSeller == null) || step == 4);
        }
    }

}
