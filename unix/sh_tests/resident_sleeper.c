#include <stdio.h>
#include <signal.h>

sigset_t set;

int main() {
    setbuf(stdout, NULL);
    sigemptyset(&set);

    printf("I'm going to sleep for eternity.\n");
    fprintf(stderr, "Whether you like it or not.\n");
    sigsuspend(&set);
    return 0;
}