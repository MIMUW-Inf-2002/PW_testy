#include <cassert>
#include <iostream>
#include <latch>
#include <algorithm>
#include "system.hpp"

#define SECOND 1000
#define START(string) cerr << (string)
#define EXCEPT(string) cerr << "caught " << (string) << " -> "
#define GOOD cerr << "GOOD" << endl
#define BAD cerr << "BAD" << endl; exit(1)
#define REPORT(shutdown) auto r = shutdown; \
if (check_reports && !check_report(r)) \
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
    size_t burgersMade;
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
                int count = 7;
                this_thread::sleep_for(chrono::seconds(1));
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

void
demo() {

    check_reports = true;
    std::vector<bool> which = {true, true, true, true};

    if (which[0]) {
        cout << "Demo Tests 1/4 - Basic (takes 17 seconds)" << endl;
        invoke([] {
            set_expected({}, {}, {}, false);
            START("CONSTRUCTOR: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
            10, 100 * SECOND};
            REPORT(system.shutdown());
            GOOD;
        });
        invoke([] {
            set_expected({{"burger", "burger"},
                          {"chips", "chips"}}, {}, {}, false);
            START("ONE WORKER, SIMPLE ORDERS: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
             1, 100 * SECOND};
            auto p1 = system.order({"burger", "burger"});
            p1->wait();
            auto o1 = system.collectOrder(std::move(p1));
            auto p2 = system.order({"chips", "chips"});
            p2->wait();
            auto o2 = system.collectOrder(std::move(p2));
            assert(checkType<Burger>(o1[0].get()));
            assert(checkType<Chips>(o2[1].get()));
            REPORT(system.shutdown());
            GOOD;
        });
        invoke([] {
            set_expected({vector<string>(15, "burger"),
                          vector<string>(10, "chips")}, {}, {}, false);
            START("ONE WORKER, BIG ORDERS: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
             1, 100 * SECOND};
            auto p1 = system.order(vector<string>(15, "burger"));
            auto p2 = system.order(vector<string>(10, "chips"));
            p1->wait();
            auto o1 = system.collectOrder(std::move(p1));
            p2->wait();
            auto o2 = system.collectOrder(std::move(p2));
            assert(checkType<Burger>(o1[14].get()));
            assert(checkType<Chips>(o2[9].get()));
            REPORT(system.shutdown());
            GOOD;
        });
        invoke([] {
            set_expected({{"burger", "burger"},
                          {"chips", "chips"}}, {}, {}, false);
            START("MULTIPLE WORKERS, SIMPLE ORDERS: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
            10, 100 * SECOND};
            auto p1 = system.order({"burger", "burger"});
            p1->wait();
            auto o1 = system.collectOrder(std::move(p1));
            auto p2 = system.order({"chips", "chips"});
            p2->wait();
            auto o2 = system.collectOrder(std::move(p2));
            assert(checkType<Burger>(o1[0].get()));
            assert(checkType<Chips>(o2[1].get()));
            REPORT(system.shutdown());
            GOOD;
        });
        invoke([] {
            int const JOBS = 30;
            vector<vector<string>> orders(JOBS / 2, {"burger"});
            orders.insert(orders.end(), JOBS / 2, {"chips"});
            set_expected(orders, {}, {}, false);
            START("MULTIPLE WORKERS, A LOT OF ORDERS: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
            10, 100 * SECOND};
            auto const flood = [&system](string const &name, uint count) {
                vector<unique_ptr<CoasterPager>> vector;
                for (uint i = 0; i < count; ++i)
                    vector.emplace_back(system.order({name}));
                return vector;
            };
            using t1 = Burger;
            using t2 = Chips;
            auto p1 = flood("burger", JOBS / 2);
            auto p2 = flood("chips", JOBS / 2);
            assert(ranges::all_of(p1.begin(), p1.end(), [&system](auto &n) {
                n->wait();
                return checkType<t1>(
                        system.collectOrder(std::move(n))[0].get());
            }));
            assert(ranges::all_of(p2.begin(), p2.end(), [&system](auto &n) {
                n->wait();
                return checkType<t2>(
                        system.collectOrder(std::move(n))[0].get());
            }));
            REPORT(system.shutdown());
            GOOD;
            cerr << '\n';
        });
    }

    if (which[1]) {
        cout << "Demo Tests 2/4 - Exceptions (takes 7 seconds)" << endl;
        invoke([] {
            bool flag = false;
            set_expected({}, {}, {}, false);
            START("ORDERED, BUT RESTAURANT IS CLOSED: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
             10, 100 * SECOND};
            REPORT(system.shutdown());
            try {
                system.order({"burger", "iceCream"});
            } catch (RestaurantClosedException const & e) {
                flag = true;
                EXCEPT("RestaurantClosedException");
            }
            if (!flag) { BAD; }
            GOOD;
        });
        invoke([] {
            set_expected({}, {{"burger", "iceCream"}}, {}, true);
            bool flag[3] = {false, false, false};
            START("FAILURE -> NON-EXISTENT PRODUCT -> UNAVAILABLE PRODUCT:\n");
            System system{
             {{"burger", shared_ptr<Machine>(new BurgerMachine())},
              {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
              {"chips", shared_ptr<Machine>(new ChipsMachine())}},
             10, 100 * SECOND};
            auto pager = system.order({"burger", "iceCream"});
            try {
                pager->wait();
                auto order = system.collectOrder(std::move(pager));
            } catch (FulfillmentFailure const & e) {
                flag[0] = true;
                EXCEPT("FulfillmentFailure");
            }
            try {
                system.order({"beatoricheeeeeeeeee"});
            } catch (BadOrderException const & e) {
                flag[1] = true;
                EXCEPT("BadOrderException");
            }
            try {
                system.order({"iceCream"});
            } catch (BadOrderException const & e) {
                flag[2] = true;
                EXCEPT("BadOrderException");
            }
            REPORT(system.shutdown());
            if (!flag[0] || !flag[1] || !flag[2]) { BAD; }
            GOOD;
        });

        invoke([] {
            set_expected({}, {}, {vector<string>(7, "burger")}, false);
            bool flag = false;
            START("NOT READY, CLIENT WAS TOO HASTY: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
             10, 1 * SECOND};
            auto p = system.order(vector<string>(7, "burger"));
            try {
                system.collectOrder(std::move(p));
            } catch (OrderNotReadyException const & e) {
                flag = true;
                EXCEPT("OrderNotReadyException");
            }
            REPORT(system.shutdown());
            if (!flag) { BAD; }
            GOOD;
        });

        invoke([] {
            set_expected({}, {}, {{"burger"}}, false);
            bool flag = false;
            START("EXPIRED, CLIENT TROLLED YOU: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
            10, 1 * SECOND};
            auto p = system.order({"burger"});
            p->wait();
            this_thread::sleep_for(chrono::seconds(2));
            try {
                system.collectOrder(std::move(p));
            } catch (OrderExpiredException const & e) {
                flag = true;
                EXCEPT("OrderExpiredException");
            }
            REPORT(system.shutdown());
            if (!flag) { BAD; }
            GOOD;
        });
        cerr << '\n';
    }

    if (which[2]) {
        invoke([] {
            cout << "Demo Tests 3/4 - Getters (takes 13 seconds)" << endl;
            set_expected({}, {}, {}, false);
            START("GET_TIMEOUT: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
            10, 100 * SECOND};
            REPORT(system.shutdown());
            assert(system.getClientTimeout() == 100 * SECOND);
            GOOD;
        });
        invoke([] {
            set_expected({}, {{"iceCream"}}, {}, true);
            START("GET_MENU: ");
            System system {
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
            10, 100 * SECOND};
            bool flag = false;
            auto menu = system.getMenu();
            assert(menu.size() == 3);
            auto pager = system.order({"iceCream"});
            try { pager->wait(); } catch (FulfillmentFailure const & e) {
                EXCEPT("FulfillmentFailure");
                flag = true;
            }
            menu = system.getMenu();
            assert(menu.size() == 2);
            system.shutdown();
            if (!flag) { BAD; }
            GOOD;
        });
        invoke([] {
            uint i = 20;
            set_expected({i, {"burger"}}, {}, {}, false);
            START("GET_PENDING_ORDERS: (pending count - expected count)\n");
            System system {
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}},
            10, 100 * SECOND};
            auto const flood = [&system](string const &name, uint count) {
                vector<unique_ptr<CoasterPager>> vector;
                for (uint i = 0; i < count; ++i)
                    vector.emplace_back(system.order({name}));
                return vector;
            };

            auto p = flood("burger", i);
            assert(system.getPendingOrders().size() == i);
            assert(ranges::all_of(p.begin(), p.end(), [&system, &i](auto &n) {
                n->wait();
                system.collectOrder(std::move(n));
                uint size = system.getPendingOrders().size();
                cerr << size << "-" << --i << ", ";
                fflush(stdout);
                return size == i;
            }));
            REPORT(system.shutdown());
            GOOD;
        });
        cerr << '\n';
    }

    if (which[3]) {
        cout << "Demo Tests 4/4 - Original (takes 2 seconds)" << endl;
        std::invoke([] {
            START("DEMO: ");
            System system {
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10, 1};
            std::latch latch(1);
            std::latch shutdown_latch(2);
            std::latch bad_latch(1);
            auto client1 = std::jthread([&system, &latch, &shutdown_latch](){
                latch.wait();
                system.getMenu();
                auto p = system.order({"burger", "chips"});
                p->wait();
                system.collectOrder(std::move(p));
                shutdown_latch.count_down();
                START("OK ");
            });
            auto client2 = std::jthread([&system, &latch, &shutdown_latch](){
                latch.wait();
                system.getMenu();
                system.getPendingOrders();
                try {
                    auto p = system.order({"iceCream", "chips"});
                    p->wait();
                    system.collectOrder(std::move(p));
                } catch (const FulfillmentFailure& e) {
                    START("OK ");
                }
                shutdown_latch.count_down();
            });
            auto client3 = std::jthread([&system, &bad_latch](){
                bad_latch.wait();
                system.getMenu();
                system.getPendingOrders();
                try {
                    auto p = system.order({"burger", "chips"});
                    p->wait();
                    system.collectOrder(std::move(p));
                } catch (const RestaurantClosedException& e) {
                    START("OK\n");
                }
            });
            latch.count_down();
            shutdown_latch.wait();
            system.shutdown();
            bad_latch.count_down();
        });
    }
}

int
main()
{
    demo();
    return 0;
}
