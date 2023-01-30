# Testy do trzeciego zadania zaliczeniowego z Programowania Współbieżnego 2022

## demo.cpp na sterydach
Na moodle'u jest umieszczony test przykładowy. W repozytorium zostawiam własną wersję
`demo.cpp` zawierającą prostsze testy i ostatecznie test oryginalny. Mam nadzieję, że
pomoże to w rozwiązaniu zadania. Skrypt działa automatycznie na studentsie, więc niczego
nie trzeba ustawiać, ale jeżeli ktoś chciałby samemu sobie skompilować:

`demo.cpp`, a także `Makefile` można sobie skopiować do swojego folderu. Kompilacja
na studentsie może być problematyczna, bo może nie istnieć plik `latch` w `/usr`.
W folderze `/opt` jest nowszy kompilator, który ma to wszystko, ale żeby wszystko
działało należy ustawić zmienną `$LD_LIBRARY_PATH` na `/opt/gcc-11.1/lib64:/opt/gcc-11.1/lib`,
za pomocą `export LD_LIBRARY_PATH=[path]` aby wskazać położenie plików `*.so`.
Szczegóły można sobie zobaczyć w kodzie. Aby uruchomić testy na swoim komputerze
warto zakomentować/ustawić zmienną `CXX` w `Makefile`.

Aby uruchomić testy demo, wystarczy przenieść pliki `system.cpp` `system.hpp` do folderu `cpp`
i uruchomić skrypt, np. poleceniem `./test.sh`. Oczekiwane wyjście jest zapisane w `demo.txt`,
program był uruchamiany na students.

## testy
Pliki zostawiamy tak jak powyżej, ale tutaj już wystarczy skompilować kod przez `make -j4`
(`-j` zależy od rdzeni i RAM), a potem wpisać `./main`, ewentualnie
`LD_LIBRARY_PATH=/opt/gcc-11.1/lib64 ./main`. Można też `./main 2>/dev/null`.
Oczekiwane wyjście jest zapisane w `expected.txt`.

- Raporty

Treść raportów jest sprawdzana przy Demo i Performance testach. Można tymczasowo wyłączyć funkcjonalność.

- Returns Tests

Sprawdza, czy rzeczywiście zwracane są wszystkie produkty. Zamawianych
sajgonek jest o wiele więcej niż przygotowana ilość, ale poprawne zamówienia nie wykraczają
poza ten limit, dlatego ważne jest, żeby sajgonki docierały w kolejności zamówień. Dodatkowo,
sajgonek jest dokładnie 100, dlatego nie można zmarnować ani jednej sajgonki. W drugim teście
jest duża szansa, że pracownicy spróbują pobrać/zwrócić produkt w tym samym czasie, wtedy
maszyna rzuca wyjątek BadGetProductException/BadReturnProductException.

- Performance Tests

Pierwsze zamówienie jest ogromne, ale błędne. Test informuje o tym, czy system od razu rezygnuje z
wykonania zamówienia po błędzie.

- Concurrent Tests

Mamy 5 klientów, którzy korzystają z systemu naraz zamiawiając szczególne produkty. Piąta osoba próbuje
narobić zamieszania wywołując resztę metod i składając puste zamówienia.
