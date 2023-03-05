#include "demo/demo.hpp"
#include "saosau/returns.hpp"
#include "saosau/performance.hpp"
#include "saosau/concurrent.hpp"
#include "saosau/edgecases.hpp"

int main() {
    demo();
    saosau::returns_test();
    saosau::performance_test();
    saosau::concurrent_test();
    saosau::edgecases_test();
    return 0;
}
