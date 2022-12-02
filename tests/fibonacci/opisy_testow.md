# Pakiet testów autorstwa Marka Lisowskiego. TE TESTY NIE MAJĄ MECHANIZMU SPRAWDZENIA ZAGŁODZENIA!

Te testy niestety nie mają nic wspólnego z Leonardem Bonaccim z Pizy.

Testy były puszczane na students rozwiązaniem bez notifyAll().

Poza RandomTest każdy test jest identyczny z każdym uruchomieniem. Oczywiście przeplot procesora wprowadza niepewności. Zawsze lepiej odpalić testy kilka razy.

Miałem 60% z polskiego, z góry przepraszam za ortografię i interpunkcję.

# OneWorkerCorrectSwitchingTest

Prosty test, który sprawdza czy dajemy robotnikowi to stanowisko o które poprosił.

# ManyWorkersOneGapTest

Prosty test, mamy wielu robotników ale nie ma szans na cykl i zawsze jest jedno wolne miejsce w warsztacie.

# ConcurrencyTest

Prosty test, który sprawdza czy wykonujemy się współbieżnie. 
Nie ma żadnych konfliktów między robotnikami.
Jeżeli test wykona się znacząco wolniej niż 2 sekundy, to znaczy że nie wykorzystujemy w pełni warsztatu.

# TwoWorkersOppositeTest

Prosty test, który sprawdza czy rozwiązujemy cykle.
Jest dwóch robotników, każdy idzie po cyklu w warszatcie ale każdy w inną stronę.

# TwoWorkersOppositeManyTimesTest

jw. tylko powtórzone wiele razy aby złapać błąd jeżeli zdarza się tylko czasami

# CycleTest

Prosty test. Mamy pracowników w cyklu który oraz pracowników chcących wejść do warsztatu na ten sam cykl.

# CycleManyTimesTest

Taka sama różnica jak w przypadku TwoWorkersOppositeTest

# WorkerStayingTest

Prosty test, w którym sprawdzamy, czy dajemy priorytet pracownikowi który zmienia stanowisko na swoje własne.

# DeadlockTwoGroupsTest

Test sprawdzający przeciwdziałanie zakleszczeniu. 
Mamy dwie grupy pracowników poruszające się w przeciwne strony w warsztacie.
Kiedy się spotkają, to mamy cykl dwuelementowy z dodatkowymi pracownikami, którzy chcą wejść na te same pola.
Jeżeli nie damy prioretetu cyklowi, to się zakleszczymy.

# DeadlockTwoGroupsManyTimesTest

jw. tylko powtórzone wiele razy

# DeadlockTreeTest

Mamy pełne drzewo binarne, o korzeniu w 1 i 64 liściach oraz dodatkowe stanowisko 0.
Jest 128 robotników, po 2 na każdy liść.
Każdy robotnik zaczyna w wyznaczonym dla niego liściu i próbuje dojść do korzenia (dzieląc numer swojego stanowska na 2).
Jak dojdzie do ojca to próbuje iść do 0 i z powrotem do liścia.
Kiedy dojdzie do 0 poraz piąty to wychodzi.
Po wejściu wszystkich robotników zawsze mamy jeden cykl, ale jest 64 możliwych cykli, i na prawie każde stanowisko w tym cyklu próbuje wejść inny robotnik.
Test daje dużo możliwości rozwiązaniu na popsucie czegoś albo zrobienie deadlocka.

# ManyWorkersChaosTest

Jest 60 robotników którzy chcą się poruszać po 20, 10 lub 4 elementowych cyklach w warsztacie o 20 stanowiskach.
Dodatkowo mamy 4 agentów chaosu którzy burzą symetrię i zwiększają szanse na dużo róznych zacykleń.

# RandomTest

Test generowany losowo i niedeterministycznie. Dlatego czas może się znacząco różnić.

# AllPermutationsTest

Warsztat o 5 stanowiskach i 120 robotników, każdy porusza się po innej permutacji 5 stanowisk.
