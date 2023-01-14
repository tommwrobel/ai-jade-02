## zadanie 1
1. Stworzylem klase Book która reprezentuje książkę.
2. w BokSellerGui dodałem pole do wprowadzanie shippingPrice
3. W kalsie BookSellerAgent w polu catalog, zamiast <title, price> mamy teraz <title, book>
4. wprowadzilem odpowiednie zmiany w kodzie, do Buyera wysyla nam sie teraz price+shippingPrice (reply.setContent(String.valueOf(book.getTotalPrice()));)

## zadanie 2
1. dodałem pole w klasie BookBuyerAgent o nazwie "budget"
2. poniżej - linijka 32. - to pole ustawia nam sie z parametrów, ktore podajemy w pliku build.xml - linijka 23.
3. modyfikacja kodu - jak znajdzie nam cene najlepszej ksiazki to na koncu sprawdza czy cena nie przewyzsza budzetu - plik BookBuyerAgent, linkijki 127-130)
4. Jesli przewyzsza, to ustawia step na 4, przez co wychodzi z operacji kupowania
5. Jesli nie przewyzsza, to kupuje ksiazke i pomniejsza budzet o cene (linijka 158 w pliku BookBuyerAgent)

## zadanie 3
1. Dodałem argument do BookSellerAgent - responseIfNotFound - który decyduje o tym, czy agent wysyła odpowiedź, jak nie znajdzie książki (domyślnie jest true)
2. Dzięki temu możemy sterować agentami i spradzać czy w przypadku braku odpowiedzi nasz program działa poprawnie.
3. Dodanie zmiennej maxIterations w BookBuyerAgent: jeśli znajdziemy książkę, to szukamy najlepszej oferty przez 10 iteracji (spradzamy czy wszyscy agencji wyslali swoje odpowiedzi)
4. Jeśli nie znaleźliśmy żadnej oferty to szukamy aż znajdziemy (tutaj logika się nie zmieniła).

## zadanie 5 - propozycja rozwiazania
1. Każda książka sellera powinna mieć również status który oznacza czy książka jest zaoferowana juz któremuś sellerowu czy nie, jesli jest to nie bierze jej pod uwagę podczas otwrzymania requesta od innego sellera.
