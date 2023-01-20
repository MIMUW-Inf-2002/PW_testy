# Testy do trzeciego zadania zaliczeniowego z Programowania Współbieżnego 2022

## demo.cpp na sterydach
Na moodle'u jest umieszczony test przykładowy. W repozytorium zostawiam własną wersję
`demo.cpp` zawierającą prostsze testy i ostatecznie test oryginalny. Mam nadzieję, że
pomoże to w rozwiązaniu zadania.

`demo.cpp`, a także `Makefile` można sobie skopiować do swojego folderu. Kompilacja
na studentsie może być problematyczna, bo może nie istnieć plik `latch.h` w `/usr`.
W folderze `/opt` jest nowszy kompilator, który ma to wszystko, ale żeby wszystko
działało należy ustawić zmienną `$LD_LIBRARY_PATH` na `/opt/gcc-11.1/lib64:/opt/gcc-11.1/lib`,
za pomocą `export LD_LIBRARY_PATH=[path]` aby wskazać położenie plików `*.so`.
Szczegóły można sobie zobaczyć w kodzie. Aby uruchomić testy na swoim komputerze
warto zakomentować zmienną `CXX` w `Makefile`.

Aby uruchomić testy, wystarczy przenieść pliki `system.cpp` `system.hpp` do folderu `cpp`
i uruchomić skrypt, np. poleceniem `./test.sh`. Oczekiwane wyjście jest zapisane w `demo.txt`,
program był uruchamiany students.
