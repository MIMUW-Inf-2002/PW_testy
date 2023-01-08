#include <stdio.h>

int main() {
    setbuf(stdin, NULL);
    printf("In and out.\n");
    fprintf(stderr, "Too fast mane.\n");
    return 0;
}