#!/bin/sh

[ -f './system.cpp' -a -f './system.hpp' ] ||
    { echo "Nie znalazłem plików."; exit 1; }

cp -v ./system.cpp ./system.hpp demo
cd demo
make
rm system.cpp system.hpp
echo
LD_LIBRARY_PATH=/opt/gcc-11.1/lib64 ./main
rm main
