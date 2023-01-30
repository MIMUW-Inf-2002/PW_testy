# To add your project do:
# SRC += ${addprefix project/, files}
SRC = main.cpp system.cpp demo/demo.cpp
SRC += ${addprefix saosau/, returns.cpp performance.cpp concurrent.cpp}

OBJ = ${SRC:.cpp=.o}
#OBJ = ${SRC_T:.cc=.o}
CXX = /opt/gcc-11.1/bin/g++-11.1
CXXFLAGS = -g -O2 -std=c++20 -I${PWD}
LDFLAGS = -lpthread

all: main

main: ${OBJ}
	${CXX} -o $@ ${OBJ} ${LDFLAGS}

.c.o:
	${CXX} -c ${CXXFLAGS} $<

clean:
	rm ${OBJ} main

.PHONY: all main clean
