Analiza danych historycznych
================

Import danych
---------------

Do importu danych służy skrypt import.sh. Skrypt wymaga konfiguracji zmiennych USER, PASS, DB. Import może trwać ponad 6 godzin.

```bash
./import.sh
```

Przygotowanie grafu
---------------
Za przygotowanie grafu odpowiedzialny jest skrypt build_graph.py w katalogu graph. Ten skrypt również wymaga konfiguracji.

```bash
graph/build_graph.sh
```

Uruchomienie aplikacji webowej
---------------

Aplikacja webowa napisana jest w Play Framework w wersji 2.3.0. Potrzebne jest ściągnięcie Activatora z tego adresu: http://www.playframework.com/download oraz dodanie ściągnietej binarki do PATH. Po zrobieniu tego:

```bash
cd web
activator
```

W konsoli activatora, wpisujemy run, aby uruchomić serwer.