package jadelab2;

import java.util.UUID;

public class Book {

    private UUID id;
    private String title;
    private int price;
    private int shippingCost;

    public Book(String title, int price, int shippingCost) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.price = price;
        this.shippingCost = shippingCost;
    }

    public UUID getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(int shippingCost) {
        this.shippingCost = shippingCost;
    }

    public int getTotalPrice() {
        return price + shippingCost;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", shippingCost=" + shippingCost +
                '}';
    }
}
