# Testy do pierwszego zadania zaliczeniowego z Programowania Współbieżnego 2022 + narzędzie do wizualizacji

Stworzone przez studentów wydziału MIM Uniwersytetu Warszawskiego.

# Uruchamiane testów


Proponujemy dwa typy uruchamiania
- do debugowania należy skopiować folder `tests` do folderu `cp2022`, a następnie uruchomić funkcję `main()` w klasie `cp2022.tests.Main`. Da się to relatywnie łatwo zrobić za pomocą IDE.
- do testowania właściwego spakowania można użyć komend: `chmod u+x ./zip_test.sh` do ustawienia uprawnień oraz `./zip_test.sh ab123456.zip` do uruchomienia testów. 

# Dodawanie testów

Zasadniczo jak ktoś ma sensowny pomysł na restrukturyzację tych testów, to zachęcamy. Na ten moment moża:
- edytować testy pggp,
- dodać nowe testy w osobnym folderze i odwołanie w `cp2022.tests.Main`.


# Narzędzie do wizualizacji
Program `vis.py` służy do odtworzenia animacji z instrukcji wyrzuconych przez oficjalny program testowy. Szczegóły w folderze `visualization`. 

Uwaga! Program może zamienić kolejność instrukcji przy wypisywaniu, stąd output może nie być miarodajny.

# Opisy testów

<details><summary>pggp</summary>

- Testy sprawdzające żywotność odczekują 10 milisekund między dwoma kolejnymi akcjami globalnie. Więc mogą zająć dużo czasu. 
  Ten czas można zmniejszyć edytując zmienną `SimulationWithBugCheck.timeOfWaitBetweenActionsWhenOrderMatters` w klasie Main,
- Do debugowania zaleca się włączenie `verbose = 2` w klasie `Main`. Powinno wystarczyć. Przy czym jeśli nie jest sprawdzana żywotność, to nie ma 100% gwarancji, że kolejność wypisań się zgadza.

| Numer testu | Kategoria                  | Nazwa Testu                                 | Opis                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | Czy sprawdzana jest żywotność? |
|-------------|----------------------------|---------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------|
| 1           | Proste                     | SimpleOneWorkplace                          | Prosty test z jednym stanowiskiem i pracownikiem, który wchodzi i wychodzi z niego kilkukrotnie.                                                                                                                                                                                                                                                                                                                                                                                                                              | ❌                              |
| 2           | Proste                     | SimpleQueue                                 | Kolejka 5 pracowników oczekuje na wejście do jednego stanowiska. Celem sprawdzenia żywotności przychodzą po sobie z opóźnieniem.                                                                                                                                                                                                                                                                                                                                                                                              | ✅                              |
| 3           | Proste                     | SimpleOneUse                                | Jeden pracownik wchodzi, używa stanowiska i wychodzi.                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | ❌                              |
| 4           | Proste                     | SimpleOneUseRepeated                        | Jak powyżej, tylko że pracownik wchodzi i wychodzi wielokrotnie.                                                                                                                                                                                                                                                                                                                                                                                                                                                              | ❌                              |
| 5           | Proste                     | SimpleSwitch                                | Jeden pracownik wchodzi i zmienia stanowiska za pomocą switchTo().                                                                                                                                                                                                                                                                                                                                                                                                                                                            | ❌                              |
| 6           | Proste                     | SimpleSwitchRepeated                        | Tak jak powyżej, tylko pracownik wchodzi i wychodzi z warsztatu kilkukrotnie.                                                                                                                                                                                                                                                                                                                                                                                                                                                 | ❌                              |
| 7           | Proste                     | SimpleSwitchAndUse                          | Jeden pracownik zmienia miejsca pracy i używa ich.                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | ❌                              |
| 8           | Proste                     | SimpleQueueAndUse                           | Pracownicy po kolei używają jednego ze stanowisk i wychodzą.                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | ✅                              |
| 9           | Proste                     | SimpleQueueInsideAndUse                     | Dwaj pracownicy używają odpowiednio stanowisk 0 i 2 oraz 1 i 2. Jeden z nich musi więc poczekać na 2.                                                                                                                                                                                                                                                                                                                                                                                                                         | ❌                              |
| 10          | Proste                     | SimpleOneStaysOneMoves                      | Jeden wchodzi i pracuje bardzo długo na jednym stanowisku. Drugi wchodzi, pracuje na drugim stanowisku i wychodzi, po czym powtarza to kilkukrotnie.                                                                                                                                                                                                                                                                                                                                                                          | ❌                              |
| 11          | Zakleszczenia              | DeadlockPair                                | Dwójka pracowników wchodzi na dwa stanowiska i chcą się zamienić.                                                                                                                                                                                                                                                                                                                                                                                                                                                             | ❌                              |
| 12          | Zakleszczenie              | DeadlockPairManyTimes                       | Poprzednie, przy czym pracownicy zamieniają się wielokrotnie.                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | ❌                              |
| 13          | Zakleszczenie              | DeadlockTriCycle                            | Cykl złożony z 3 pracowników.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | ❌                              |
| 14          | Zakleszczenie              | DeadlockTriCycleManyTimes                   | Analogicznie jak powyżej, tylko kręcenie w cyklu trwa dłużej.                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | ❌                              |
| 15          | Zakleszczenie              | DeadlockOneBigOneSmallCycleWithCommonVertex | Jeden cykl 3-elementowy, jeden 2-elementowy z jednym wspólnym elementem.                                                                                                                                                                                                                                                                                                                                                                                                                                                      | ❌                              |
| 16          | Zagłodzenie                | StarvationTricycleAndQueue                  | Jeden 3-cykl oraz bardzo długa kolejka do 4 stanowiska. UWAGA! Test zakłada, że jeśli jeden użytkownik wejdzie do warsztatu wielokrotnie to liczy się jako inny.                                                                                                                                                                                                                                                                                                                                                              | ✅                              |
| 17          | Zagłodzenie                | StarvationOneLongQueue                      | Jedna duża kolejka przed wejściem do jednego stanowiska.                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | ✅                              |
| 18          | Zagłodzenie                | StarvationStar                              | 3 wierzchołki skaczą pomiędzy stanowiskiem 0 i jednym ze stanowisk spośród 1, 2, 3, innym dla każdego z nich. Poza tym do stanowiska 4 jest długa kolejka.                                                                                                                                                                                                                                                                                                                                                                    | ✅                              |
| 19          | Zagłodzenie                | StarvationManyQueues                        | Jest kilka długich kolejek.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | ✅                              |
| 20          | Zagłodzenie                | StarvationBigStar                           | Są 102 stanowiska, na stanowisko 0 swobodnie wchodzą ludzie i wychodzą. Na stanowiska 1-100 weszli ludzie. Chcą zmienić na stanowisko 101 i wyjść.                                                                                                                                                                                                                                                                                                                                                                            | ✅                              |
| 21          | Wydajność                  | EfficiencyParallel                          | Jest 5 stanowisk i po 1 osobie, która chce wejść na każde z nich. Praca trwa 500ms. Limit czasu: 1 sekunda.                                                                                                                                                                                                                                                                                                                                                                                                                   | ❌                              |
| 22          | Wydajność                  | EfficiencyCycle                             | Dużo rzeczy, które chcą chodzić po tym samym 5-cyklu. Z limitem czasu.                                                                                                                                                                                                                                                                                                                                                                                                                                                        | ❌                              |
| 23          | Duże i losowe              | BigRandomRotations                          | Jest 100 stanowisk i 100 osób wchodzi i robi po 10 losowych switchy, po czym wychodzi.                                                                                                                                                                                                                                                                                                                                                                                                                                        | ❌                              |
| 24          | Duże i losowe              | BigRandom1                                  | Jest 100 pracowników, 3 stanowiska i każdy chce zrobić po 100 losowych akcji.                                                                                                                                                                                                                                                                                                                                                                                                                                                 | ❌                              |
| 25          | Duże i losowe              | BigRandom2                                  | Podobne jak wyżej, tylko z nieco innymi parametrami np. jak często ludzie wychodzą.                                                                                                                                                                                                                                                                                                                                                                                                                                           | ❌                              |
| 26          | Duże i losowe, zagłodzenie | BigRadnsomStarvation                        | Jest 10 pracowników, 5 stanowisk i każdy chce zrobić po 100 losowych akcji.                                                                                                                                                                                                                                                                                                                                                                                                                                                   | ✅                              |
| 27          | Duże i losowe              | BigRandom3                                  | Jest 100 pracowników, 10 stanowisk i każdy chce zrobić po 100 losowych akcji.                                                                                                                                                                                                                                                                                                                                                                                                                                                 | ❌                              |
| 28          | Duże i losowe              | BigRandom4                                  | Jest 1000 pracowników, 50 stanowisk i każdy chce zrobić po 10 losowych akcji.                                                                                                                                                                                                                                                                                                                                                                                                                                                 | ❌                              |
| 29          | Duże i losowe              | BigRandom5                                  | Jest 100 pracowników, 1000 stanowisk i każdy chce zrobić po 10000 losowych akcji. Czas pracy ustawiony na 0.                                                                                                                                                                                                                                                                                                                                                                                                                  | ❌                              |
| 30          | Wydajność                  | Test30EfficiencyOrderErrorCatch             | Test sprawdza, czy nie zaimplementowano ,,którzy zaczęli chcieć wejść po tym, gdy on zaczął chcieć, odpowiednio, wejść lub zmienić stanowisko''. Najpierw wchodzi osoba A na stanowisko 1 i czeka tam 1 sekundę. Następnie przychodzi osoba B, która chce wejść na stanowisko 1 i poczekać 10 sekund. Potem 1000 osób ustawia się w kolejce do stanowiska 0. Po sekundzie osoba B powinna od razu wejść na stanowisko A, a nie czekać na kolejkę. Stąd test powinien zająć trochę więcej niż 11 sekund - ustawiono limit 15s. | ❌                              |
  </details>


<details><summary>kwasow</summary>

Do debugowania zalecam ustawienie `verbose = true` w klasie `KwasowMain`. Nie ma gwarancji, że
kolejność wypisywania jest prawidłowa ze względu na przeploty. Output jest kompatybilny
z narzędziem do wizualizacji.

Opisy testów znajdują się w klasie `KwasowMain`.

Testy zawsze kończą się kodem 0. Jeśli pojawi się deadlock, to testy nie kończą się. Jeśli zajdzie
sytuacja niedozwolona, to wypisują wyjątek.

</details>