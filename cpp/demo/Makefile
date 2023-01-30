SRC = main.cpp demo.cpp system.cpp
OBJ = ${SRC:.cpp=.o}
# CXX = /opt/gcc-11.1/bin/g++-11.1
CXXFLAGS = -O2 -std=c++20
LDFLAGS = -lpthread

all: main clean

main: ${OBJ}
	${CXX} -o $@ ${OBJ} ${LDFLAGS}

.c.o:
	${CXX} -c ${CXXFLAGS} $<

clean:
	rm ${OBJ}

.PHONY: all main clean
