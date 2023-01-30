#include <cassert>
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

bool check_reports;
vector<string> fulfilled_orders;
vector<string> failed_orders;
vector<string> abandoned_orders;
bool failed_products; // Only ice_cream machine fails

string
get_code(vector<string> const & order)
{
    string code;
    int burgers, chips, ice_creams, garbage;

    burgers = chips = ice_creams = garbage = 0;
    for (string const & product: order) {
        if (product == "burger")
            ++burgers;
        else if (product == "chips")
            ++chips;
        else if (product == "iceCream")
            ++ice_creams;
        else
            ++garbage;
    }
    code.push_back((char) burgers);
    code.push_back((char) chips);
    code.push_back((char) ice_creams);
    code.push_back((char) garbage);

    return code;
}

void
set_expected(vector<vector<string>> const & fulfilled,
             vector<vector<string>> const & failed,
             vector<vector<string>> const & abandoned, bool ice_cream)
{
    fulfilled_orders.clear();
    failed_orders.clear();
    abandoned_orders.clear();
    for (vector<string> const & order: fulfilled)
        fulfilled_orders.emplace_back(get_code(order));
    for (vector<string> const & order: failed)
        failed_orders.emplace_back(get_code(order));
    for (vector<string> const & order: abandoned)
        abandoned_orders.emplace_back(get_code(order));
    failed_products = ice_cream;
}

bool
check_report(vector<WorkerReport> const & reports)
{
    vector<string> r1, r2, r3;
    bool r4 = false; // True, if failed product exists.

    sort(fulfilled_orders.begin(), fulfilled_orders.end());
    sort(failed_orders.begin(), failed_orders.end());
    sort(abandoned_orders.begin(), abandoned_orders.end());

    for (WorkerReport const & report: reports) {
        for (vector<string> const & order: report.collectedOrders)
            r1.emplace_back(get_code(order));
        for (vector<string> const & order: report.failedOrders)
            r2.emplace_back(get_code(order));
        for (vector<string> const & order: report.abandonedOrders)
            r3.emplace_back(get_code(order));
        if (!report.failedProducts.empty())
            r4 = true;
    }

    sort(r1.begin(), r1.end());
    sort(r2.begin(), r2.end());
    sort(r3.begin(), r3.end());

    // Breakpoint here if you have problems with reports.
    return fulfilled_orders == r1 && failed_orders == r2 &&
        abandoned_orders == r3 && failed_products == r4;
}

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
        EXCEPT("IncompetentFoolException " + to_string(count) + " times");
        system.shutdown();
        GOOD;
    }
}
