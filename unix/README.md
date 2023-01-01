# Testy do drugiego zadania zaliczeniowego z Programowania Współbieżnego 2022/2023

Stworzone przez studentów wydziału MIM Uniwersytetu Warszawskiego.

# Proste testy na format wyjścia

Aby uruchomić program `test.py`, trzeba podać ścieżkę do folderu z plikem Cmake w następujący sposób:
```
usage: test.py path_to_src [-h] [-f F] [-n N] [-vn VN] [-hn HN]

positional arguments:
  path_to_src  path to directory with cmake file

options:
  -h, --help   show this help message and exit
  -f F         path to file with input, if you want to test only one file
  -n N         number of output comparison tests
  -vn VN       number of valgrind tests
  -hn HN       number of helgrind tests
  ```

Czyli na przykład:

```python3 test.py <path/to/src>```

```python3 test.py <path/to/src> -f <path/to/test/file>```

Konieczne jest uruchomienie `test.py` z katalogu, w którym on się znajduje. Należy miec na uwadze, że program porównuje wyjścia oraz to, że różne interpetacje zadania mogą powodować różne wyjścia, niepasujące do szablonów (zachęcam do dyskusji). Niezmodyfikowane wyjścia wyrzucone przez executora znajdują się w folderze z buildem, również po zakończeniu programu.

Dla twórców testów, testarka najpierw kompiluje wszystkie programy znajdujące się w danym folderze z testami, tak, że można w różnych testach korzystać z różnych programów. Można również odpalić testy pliku .out. Szablony wyjść mają specjalne linijki "=====", pomiędzy nimi wyjście może być w różnej kolejności w stosunku do poprawnego.

Zachęcam do modyfikacji testarki, jak ktoś ma fajny pomysł.
