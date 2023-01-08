#include <stdio.h>
#include <unistd.h>

int main() {
    setbuf(stdin, NULL);
    printf("Let me take a nap...\n");
    fprintf(stderr, "Just 3 seconds.\n");
    sleep(3);
    return 0;
}
