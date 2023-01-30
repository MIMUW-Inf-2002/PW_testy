#include <iostream>
#include <algorithm>
#include "system.hpp"

#define SECOND 1000
#define START(string) cerr << (string)
#define EXCEPT(string) cerr << "caught " << (string) << " -> "
#define GOOD cerr << "GOOD" << endl
#define BAD cerr << "BAD" << endl; exit(1)
#define REPORT(shutdown) auto r = shutdown; \
if (!check_report(r) && check_reports) \
    throw BadReportException()

using namespace std;

namespace {
template <typename T, typename V>
bool checkType(const V* v) {
    return dynamic_cast<const T*>(v) != nullptr;
}

class BadReportException: public exception {};

class Burger : public Product {};
class IceCream : public Product {};
class Chips : public Product {};
class BurgerMachine : public Machine {
    std::atomic<size_t> burgersMade;
    // Prędkość: 50 burgerów na sekundę!
    chrono::milliseconds time = chrono::milliseconds(20);
public:
    BurgerMachine() : burgersMade(0) {}
    unique_ptr<Product> getProduct() override {
        this_thread::sleep_for(time);
        return unique_ptr<Product>(new Burger());
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<Burger>(product.get())) throw BadProductException();
        burgersMade++;
    }
    void start() override {}
    void stop() override {}
};

class IceCreamMachine : public Machine {
public:
    unique_ptr<Product> getProduct() override {
        throw MachineFailure();
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<IceCream>(product.get())) throw BadProductException();
    }
    void start() override {}
    void stop() override {}
};

class ChipsMachine : public Machine {
    std::thread thread;
    std::mutex mutex;
    condition_variable cond;
    atomic<int> wcount;
    deque<unique_ptr<Chips>> queue;
    atomic<bool> running;
public:
    ChipsMachine() : running(false) {}
    unique_ptr<Product> getProduct() override {
        if (!running) throw MachineNotWorking();
        wcount++;
        unique_lock<std::mutex> lock(mutex);
        cond.wait(lock, [this](){ return !queue.empty(); });
        wcount--;
        auto product = std::move(queue.front());
        queue.pop_front();
        return product;
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<Chips>(product.get())) throw BadProductException();
        if (!running) throw MachineNotWorking();
        lock_guard<std::mutex> lock(mutex);
        queue.push_front((unique_ptr<Chips>&&) (std::move(product)));
        cond.notify_one();
    }
    void start() override {
        running = true;
        thread = std::thread([this](){
            while (running || wcount > 0)
            {
                // Prędkość: 50 frytek na sekundę, ale z czkawkami...
                int count = 7;
                this_thread::sleep_for(chrono::milliseconds(20 * count));
                {
                    lock_guard<std::mutex> lock(mutex);
                    while (count --> 0) {
                        queue.push_back(make_unique<Chips>());
                        cond.notify_one();
                    }
                }
            }
        });
    }
    void stop() override {
        running = false;
        thread.join();
    }
};
}

namespace saosau {
    void concurrent_test() {
        cerr << '\n';
        cout << "Concurrent Tests (takes 8-9 seconds)" << endl;
        int count = 0;
        int const hunger = 100;
        std::atomic<int> left(400);
        System system{
        {{"burger", shared_ptr<Machine>(new BurgerMachine())},
         {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
         {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10, 100};
        vector<string> rudolf_order = {"burger", "chips", "burger", "burger"};
        vector<string> eva_order = {"chips", "burger", "chips", "chips"};
        vector<string> rosa_order = {"chips", "chips", "chips", "iceCream"};
        vector<string> krauss_order = {"iceCream", "iceCream", "iceCream"};
        auto const fool = [&system, &count, &left]
                (vector<string> const & order) {
            for (uint i = 0; i < hunger; ++i) {
                --left;
                try {
                    auto p = system.order(order);
                    p->wait();
                    system.collectOrder(std::move(p));
                } catch (...) {
                    count++;
                }
            }
        };
        auto const oh_desire = [&system, &left] {
            while (left != 0) {
                system.getMenu();
                system.getPendingOrders();
                system.getClientTimeout();
                try { system.order({}); } catch (BadOrderException const &) {}
                printf("\33[2K\rOrders left: %d", left.load());
                fflush(stdout);
                this_thread::sleep_for(chrono::milliseconds(50));
            }
        };
        thread krauss(fool, krauss_order);
        thread rudolf(fool, rudolf_order);
        thread eva(fool, eva_order);
        thread rosa(fool, rosa_order);
        thread kinzou(oh_desire);
        krauss.join();
        rudolf.join();
        eva.join();
        rosa.join();
        kinzou.join();
        printf("\33[2K\r");
        fflush(stdout);
        START("CONCURRENT: ");
        EXCEPT("exception " + to_string(count) + " times");
        system.shutdown();
        if (count != 200) { BAD; }
        GOOD;
    }
}
