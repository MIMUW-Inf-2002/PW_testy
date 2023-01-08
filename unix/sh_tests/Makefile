SRC = resident_sleeper.c ninja.c napper.c
OUT = ${SRC:.c=}
CFLAGS = -g -Wall -Wextra -Wno-sign-compare -std=gnu11
LDFLAGS =

all: ${OUT}

options:
	@echo build options:
	@echo "CFLAGS   = ${CFLAGS}"
	@echo "LDFLAGS   = ${LDFLAGS}"
	@echo "CC       = ${CC}"

% : %.c
	${CC} ${CFLAGS} ${LDFLAGS} -o $@ $<

clean:
	rm ${OUT}
	rm test?.out
	rm fifo
