#include <cassert>
#include <iostream>
#include <algorithm>
#include "system.hpp"

#define SECOND 1000
#define START(string) cerr << (string)
#define EXCEPT(string) cerr << "caught " << (string) << " -> "
#define GOOD cerr << "GOOD" << endl
#define BAD cerr << "BAD" << endl; exit(1)
#define REPORT(shutdown) if (!check_report(shutdown) && check_reports) \
    throw BadReportException()

using namespace std;

namespace {
template <typename T, typename V>
bool checkType(const V* v) {
    return dynamic_cast<const T*>(v) != nullptr;
}

class Burger : public Product {};
class IceCream : public Product {};
class Rice: public Product {};
class BurgerMachine : public Machine {
    std::atomic<size_t> burgersMade;
    chrono::seconds time = chrono::seconds(1);
public:
    BurgerMachine() : burgersMade(0) {}
    unique_ptr<Product> getProduct() override {
        if (burgersMade > 0) {
            burgersMade--;
            return unique_ptr<Product>(new Burger());
        } else {
            this_thread::sleep_for(time);
            return unique_ptr<Product>(new Burger());
        }
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<Burger>(product.get())) throw BadProductException();
        burgersMade++;
    }
    void start() override {
        burgersMade = 10;
    }
    void stop() override {}
};

class IceCreamMachine : public Machine {
public:
    unique_ptr<Product> getProduct() override {
        this_thread::sleep_for(chrono::milliseconds(20));
        throw MachineFailure();
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<IceCream>(product.get())) throw BadProductException();
    }
    void start() override {}
    void stop() override {}
};

class RiceCooker: public Machine {
public:
    unique_ptr<Product> getProduct() override {
        return make_unique<Rice>();
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<Rice>(product.get())) throw BadProductException();
    }
    void start() override {}
    void stop() override {}
};
}

namespace saosau {
void edgecases_test() {
    cerr << '\n';
    cout << "Edge Cases Tests (instant)" << endl;
    /* Wolałbym rzucić illegal argument exception, ale tak naprawdę, to
     * zgodnie z zadaniem test powinien się zakonczyć deadlockiem. */
    // invoke([]{
    //     bool flag[2] = {false, false};
    //     START("NO WORKERS: ");
    //     System system {
    //         {{"burger", shared_ptr<Machine>(new BurgerMachine())},
    //          {"iceCream", shared_ptr<Machine>(new IceCreamMachine())}},
    //          0, 1 * SECOND
    //     };
    //     auto p1 = system.order({"burger"});
    //     auto p2 = system.order({"iceCream"});
    //     try {
    //         auto p3 = system.order({"chips"});
    //     } catch (BadOrderException const &) {
    //         EXCEPT("BadOrderException");
    //         flag[0] = true;
    //     }
    //     p2->wait(50);
    //     try {
    //         system.collectOrder(std::move(p2));
    //     } catch (OrderNotReadyException const &) {
    //         EXCEPT("OrderNotReadyException");
    //         flag[1] = true;
    //     }
    //     auto reports = system.shutdown();
    //     assert(reports.empty());
    //     if (!flag[0] || !flag[1]) { BAD; }
    //     GOOD;
    // });
    invoke([]{
        bool flag = false;
        START("NO MACHINES: ");
        System system {{}, 100, 100};
        try {
            auto p = system.order({"burger"});
        } catch (BadOrderException const &) {
            EXCEPT("BadOrderException");
            flag = true;
        }
        auto reports = system.shutdown();
        for (auto const & r: reports) {
            assert(r.collectedOrders.empty());
            assert(r.failedOrders.empty());
            assert(r.abandonedOrders.empty());
            assert(r.failedProducts.empty());
        }
        if (!flag) { BAD; }
        GOOD;
    });
    invoke([]{
        START("REPEATED SHUTDOWNS: ");
        System system {
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())}},
             10, 100
        };
        system.shutdown();
        system.shutdown();
        GOOD;
    });
    invoke([] {
        bool flag[3] = {false, false, false};
        START("FAILURE ALL STAGES: ");
        System system {
            {{"iceCream", shared_ptr<Machine>(new IceCreamMachine())}},
             10, 10 * SECOND
        };
        auto p = system.order({"iceCream"});
        try {
            p->wait();
        } catch (FulfillmentFailure const &) {
            EXCEPT("FulfillmentFailure");
            flag[0] = true;
        }
        try {
            p->wait(1000);
        } catch (FulfillmentFailure const &) {
            EXCEPT("FulfillmentFailure");
            flag[1] = true;
        }
        try {
            system.collectOrder(std::move(p));
        } catch (FulfillmentFailure const &) {
            EXCEPT("FulfillmentFailure");
            flag[2] = true;
        }
        system.shutdown();
        if (!flag[0] || !flag[1] || !flag[2]) { BAD; }
        GOOD;
    });
    invoke([] {
        bool flag = false;
        START("NULL PAGER: ");
        System system {
            {{"burger", shared_ptr<Machine>(new BurgerMachine())}},
             10, 10 * SECOND
        };
        try {
            system.collectOrder(nullptr);
        } catch (BadPagerException const &) {
            EXCEPT("BadPagerException");
            flag = true;
        }
        system.shutdown();
        if (!flag) { BAD; }
        GOOD;
    });

    /* Treść zadania gwarantuje, że taki case się nie zdarzy, ale
     * może być to dobry sanity check na warunek, zeby nie oddawać
     * zamówienia o numerze, ktory już został odebrany.
     * Chyba, że ktoś wpadnie na pomysł jak obejść domyślny deleter
     * klasy unique_ptr. */
    // invoke([] {
    //     bool flag = false;
    //     START("FAKE PAGER: ");
    //     System system {
    //         {{"burger", shared_ptr<Machine>(new BurgerMachine())}},
    //          1, 10 * SECOND // Umyslnie daje tylko 1 pracownika.
    //     };
    //     System fake_system {
    //         {{"burger", shared_ptr<Machine>(new BurgerMachine())}},
    //          1, 1
    //     };
    //     auto pager = system.order(vector<string>(10, "burger"));
    //     auto fake_pager = fake_system.order({"burger"});
    //     fake_pager->wait();
    //     pager->wait();
    //     system.collectOrder(std::move(fake_pager));
    //     try {
    //         system.collectOrder(std::move(pager));
    //     } catch (BadPagerException const &) {
    //         EXCEPT("BadPagerException");
    //         flag = true;
    //     }
    //     system.shutdown();
    //     fake_system.shutdown();
    //     if (!flag) { BAD; }
    //     GOOD;
    // });

    /* Ja bym rzucił overflow error. Tak naprawdę, to nigdy tego testu
     * nie odpaliłem i nie polecam. */
    // invoke([] {
    //     START("ORDERS OVERFLOW: ");
    //     System system {
    //         {{"rice", shared_ptr<Machine>(new RiceCooker())}},
    //          10, 3600 * SECOND
    //     };
    //     auto pager1 = system.order({"rice"});
    //     uint current;
    //     do {
    //         auto p = system.order({"rice"});
    //         current = p->getId();
    //         p->wait();
    //         system.collectOrder(std::move(p));
    //     } while (current != pager1->getId() - 1);
    //     current = pager1->getId();
    //     auto pager2 = system.order({"rice"});
    //     assert(system.getPendingOrders() == {current, current});
    //     system.shutdown();
    //     system.collectOrder(std::move(pager));
    //     GOOD;
    // });
    
    /* Ostatni sanity check, jeżeli mamy jednego pracownika, to
     * ryż powinniśmy dostać dopiero po 3 sekundach, a jeżeli
     * 14, to od razu. */
    // invoke([] {
    //     chrono::steady_clock sc;
    //     static uint const workers = 1; // Tutaj zmienic
    //     static uint minimum_time = workers == 14 ? 0 : 2;
    //     START("WORKERS ACTUALLY EXIST: ");
    //     System system {
    //         {{"rice", shared_ptr<Machine>(new RiceCooker())},
    //          {"burger", shared_ptr<Machine>(new BurgerMachine())}},
    //         workers, 1
    //     };
    //     auto start = sc.now();
    //     vector<unique_ptr<CoasterPager>> pager;
    //     for (uint i = 0; i < 13; ++i)
    //         pager.emplace_back(system.order({"burger"}));
    //     jthread jthr([&pager, &system] {
    //         for (auto & n: pager) {
    //             n->wait();
    //             system.collectOrder(std::move(n));
    //         }
    //     });
    //     auto p = system.order({"rice"});
    //     p->wait();
    //     auto time = static_cast<chrono::duration<double>>(sc.now() - start);
    //     START(to_string(time.count()) + " -> ");
    //     system.shutdown();
    //     if ((uint) time.count() < minimum_time) { BAD; }
    //     GOOD;
    // });
}
}
