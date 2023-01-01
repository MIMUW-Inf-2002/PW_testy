# Testy do drugiego zadania zaliczeniowego z Programowania Współbieżnego 2022/2023

Stworzone przez studentów wydziału MIM Uniwersytetu Warszawskiego.

# Proste testy na format wyjścia

Są dwie opcje uruchomienia programu `test.py`:

```python3 test.py <path/to/src>```

```python3 test.py <path/to/src> <path/to/test/file> N_REPEAT```

Konieczne jest uruchomienie `test.py` z katalogu, w którym on się znajduje. Należy miec na uwadze, że program porównuje wyjścia oraz to, że różne interpetacje zadania mogą powodować różne wyjścia, niepasujące do szablonów (zachęcam do dyskusji). Niezmodyfikowane wyjścia wyrzucone przez executora znajdują się w folderze z buildem, również po zakończeniu programu.

Dla twórców testów, testarka najpierw kompiluje wszystkie programy znajdujące się w danym folderze z testami, tak, że można w różnych testach korzystać z różnych programów. Można również odpalić testy bez pliku z poprawnym wyjściem. Szablony wyjść mają specjalne linijki "=====", pomiędzy nimi wyjście może być w różnej kolejności w stosunku do poprawnego.

Zachęcam do modyfikacji testarki, jak ktoś ma fajny pomysł.
