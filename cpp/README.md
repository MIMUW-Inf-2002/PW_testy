# Testy do trzeciego zadania zaliczeniowego z Programowania Współbieżnego 2022

Testy korzystają z biblioteki latch, której nie ma na obecnym Debianie.

## Jak uruchomić testy (Makefile)
1. W pliku tekstowym `Makefile` należy ustawić zmienną CXX.
Jeżeli twój domyślny kompilator jest wystarczająco nowy,
zakomentuj CXX. Jeżeli masz inny kompilator, ustaw CXX na
jego ścieżkę. Jeżeli jesteś na students ustaw
CXX na /opt/gcc-11.1/bin/g++-11.1 (domyślnie).

2. Jeżeli skorzystałeś z kompilatora /opt/gcc-11.1,
wywołaj polecenie `export LD_LIBRARY_PATH=/opt/gcc-11.1/lib64`.
Z innym kompilatorem będzie analogicznie. Jeżeli korzystasz z
domyślnego, pomiń ten krok.

3. Rozpakuj paczkę ab123456.zip w folderze `cpp` i skompiluj
poleceniem `make -j4`. Plik wykonywalny to `main`.

4. `main.cpp` i każdy test oddzielnie można dowolnie edytować.
Aby wprowadzić zmiany powtórz krok 3.

`for i in $(seq 100); do echo "$i"; ./main 2>/dev/null || break; done`

## Testy

<details><summary>saosau</summary>
  
- Demo i Raporty

Podstawowa funkcjonalność systemu. Treść raportów jest sprawdzana
przy Demo i Performance testach. Można tymczasowo wyłączyć raporty.

- Returns Tests

Sprawdza, czy rzeczywiście zwracane są wszystkie produkty. Zamawianych
sajgonek jest o wiele więcej niż przygotowana ilość, ale poprawne
zamówienia nie wykraczają poza ten limit. Dodatkowo, sajgonek jest
dokładnie 100. W drugim teście jest duża szansa, że pracownicy spróbują
pobrać/zwrócić sajgonkę w tym samym czasie, wtedy
maszyna rzuca wyjątek UnsafeGetProductException/UnsafeReturnProductException.

- Performance Tests

Pierwsze zamówienie jest ogromne, ale błędne. Test informuje o tym,
czy system od razu rezygnuje z wykonania zamówienia po błędzie.

- Concurrent Tests

Mamy 5 klientów, którzy korzystają z systemu naraz zamiawiając
szczególne produkty. Piąta osoba próbuje narobić zamieszania
wywołując resztę metod i składając puste zamówienia.

- Edge Cases Tests

Niektóre testy są wykomentowane ze względu na specyfikę treści zadania.
Mimo to, jednak warto spróbować przez nie przejść, przynajmniej wtedy
jesteśmy świadom jak nasz program reaguje na dziwne sytuacje.

_W wypadku błędu testy kończą program niezerowym statusem._
</details>
