//zadanie 1
1. Stworzylem klase Book która reprezentuje książkę.
2. w BokSellerGui dodałem pole do wprowadzanie shippingPrice
3. W kalsie BookSellerAgent w polu catalog, zamiast <title, price> mamy teraz <title, book>
4. wprowadzilem odpowiednie zmiany w kodzie, do Buyera wysyla nam sie teraz price+shippingPrice (reply.setContent(String.valueOf(book.getTotalPrice()));)

//zadanie 2
1. dodałem pole w klasie BookBuyerAgent o nazwie "budget"
2. poniezej - linijka 32. - to pole ustawia nam sie z parametrów, ktore podajemy w pliku build.xml - linijka 23. (ustawiam to na 100, mozesz inaczej)
3. modyfikuej kod - jak znajdzie nam cene najlepszej ksiazki to na koncu spradza czy cena nie przewyzsza budzetu plik BookBuyerAgent, linkijki 127-130)
4. Jesli przewyzsza, to ustawia step na 4, przez co wychodzi z operacji kupowania
5. Jesli nie przewyzsza, to kupuje ksiazke i pomniejsza budzet o cene (linijka 158 w pliku BookBuyerAgent)

//zadanie 3
1. Dodałem argument do BookSellerAgent - responseIfNotFound - który decyduje o tym, czy agent wysyła odpowiedź, jak nie znajdzie książki (domyślnie jest true)
2. Dzięki temu możemy sterować agentami i spradzać czy w przypadku braku odpowiedzi nasz program działa poprawnie.
3. Dodanie zmiennej maxIterations - jeśli znajdziemy jakiegoś agenta z szukaną książką, ale nie dostaliśmy jeszcze wszystkich odpowiedzi - jeśli ilość iteracji szukania nie przekracza maxIterations (10 domyślnie) - przerywamy szukanie i kupujemy od agenta.