#include <cassert>
#include <iostream>
#include <latch>
#include <algorithm>
#include "system.hpp"

#define START(string) cout << (string); fflush(stdout)
#define EXCEPT(string) cout << "caught " << (string) << " -> "; fflush(stdout)
#define GOOD cout << "GOOD" << endl
#define BAD cout << "BAD" << endl

using namespace std;

namespace {
template <typename T, typename V>
bool checkType(const V* v) {
    return dynamic_cast<const T*>(v) != nullptr;
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

void demo() {
    std::vector<bool> which = {true, true, true, true};

    if (which[0]) {
        cout << "============== BASIC ==============" << endl;
        invoke([] {
            START("CONSTRUCTOR: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10,
            100};
            system.shutdown();
            GOOD;
        });
        invoke([] {
            START("ONE WORKER, SIMPLE ORDERS: ");
            System system{
                    {{"burger", shared_ptr<Machine>(new BurgerMachine())},
                     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
                     {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 1,
                    100};
            auto p1 = system.order({"burger", "burger"});
            p1->wait();
            auto o1 = system.collectOrder(std::move(p1));
            auto p2 = system.order({"chips", "chips"});
            p2->wait();
            auto o2 = system.collectOrder(std::move(p2));
            assert(checkType<Burger>(o1[0].get()));
            assert(checkType<Chips>(o2[1].get()));
            system.shutdown();
            GOOD;
        });
        invoke([] {
            START("ONE WORKER, BIG ORDERS: ");
            System system{
                    {{"burger", shared_ptr<Machine>(new BurgerMachine())},
                     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
                     {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 1,
                    100};
            auto p1 = system.order(vector<string>(15, "burger"));
            auto p2 = system.order(vector<string>(10, "chips"));
            p1->wait();
            auto o1 = system.collectOrder(std::move(p1));
            p2->wait();
            auto o2 = system.collectOrder(std::move(p2));
            assert(checkType<Burger>(o1[14].get()));
            assert(checkType<Chips>(o2[9].get()));
            system.shutdown();
            GOOD;
        });
        invoke([] {
            START("MULTIPLE WORKERS, SIMPLE ORDERS: ");
            System system{
                    {{"burger", shared_ptr<Machine>(new BurgerMachine())},
                     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
                     {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10,
                    100};
            auto p1 = system.order({"burger", "burger"});
            p1->wait();
            auto o1 = system.collectOrder(std::move(p1));
            auto p2 = system.order({"chips", "chips"});
            p2->wait();
            auto o2 = system.collectOrder(std::move(p2));
            assert(checkType<Burger>(o1[0].get()));
            assert(checkType<Chips>(o2[1].get()));
            system.shutdown();
            GOOD;
        });
        invoke([] {
            START("MULTIPLE WORKERS, A LOT OF ORDERS: ");
            int const JOBS = 30;
            System system{
                    {{"burger", shared_ptr<Machine>(new BurgerMachine())},
                     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
                     {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10,
                    100};
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
            system.shutdown();
            GOOD;
        });
    }

    if (which[1]) {
        cout << "============== BASIC EXCEPTIONS ==============" << endl;
        invoke([] {
            START("ORDERED, BUT RESTAURANT IS CLOSED: ");
            bool flag = false;
            System system{
                    {{"burger", shared_ptr<Machine>(new BurgerMachine())},
                     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
                     {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10,
                    100};
            system.shutdown();
            try {
                system.order({"burger", "iceCream"});
            } catch (RestaurantClosedException const & e) {
                flag = true;
                EXCEPT("RestaurantClosedException"); GOOD;
            }
            if (!flag) BAD;
        });
        invoke([] {
            START("FAILURE -> NON-EXISTENT PRODUCT -> UNAVAILABLE PRODUCT:\n");
            bool flag[3] = {false, false, false};
            System system{
                    {{"burger", shared_ptr<Machine>(new BurgerMachine())},
                     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
                     {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10,
                    100};
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
            system.shutdown();
            if (flag[0] && flag[1] && flag[2]) GOOD;
            else BAD;
        });

        invoke([] {
            START("NOT READY, CLIENT WAS TOO HASTY: ");
            bool flag = false;
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10, 1};
            auto p = system.order(std::vector<std::string>(7, "burger"));
            try {
                system.collectOrder(std::move(p));
            } catch (OrderNotReadyException const & e) {
                flag = true;
                EXCEPT("OrderNotReadyException"); GOOD;
            }
            if (!flag) BAD;
            system.shutdown();
        });

        invoke([] {
            START("EXPIRED, CLIENT TROLLED YOU: ");
            bool flag = false;
            System system{
                    {{"burger", shared_ptr<Machine>(new BurgerMachine())},
                     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
                     {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10,
                    1};
            auto p = system.order({"burger"});
            p->wait();
            this_thread::sleep_for(chrono::seconds(2));
            try {
                system.collectOrder(std::move(p));
            } catch (OrderExpiredException const & e) {
                flag = true;
                EXCEPT("OrderExpiredException"); GOOD;
            }
            if (!flag) BAD;
            system.shutdown();
        });
    }

    if (which[2]) {
        cout << "============== GETTERS ==============" << endl;
        invoke([] {
            START("GET_TIMEOUT: ");
            System system{
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10, 100};
            system.shutdown();
            assert(system.getClientTimeout() == 100);
            GOOD;
        });
        invoke([] {
            START("GET_MENU: ");
            System system {
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10, 100};
            auto menu = system.getMenu();
            assert(menu.size() == 3);
            auto pager = system.order({"iceCream"});
            pager->wait();
            menu = system.getMenu();
            assert(menu.size() == 2);
            system.shutdown();
            GOOD;
        });
        invoke([] {
            START("GET_PENDING_ORDERS: (pending count - expected count)\n");
            System system {
            {{"burger", shared_ptr<Machine>(new BurgerMachine())},
             {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
             {"chips", shared_ptr<Machine>(new ChipsMachine())}}, 10, 100};
            auto const flood = [&system](string const &name, uint count) {
                vector<unique_ptr<CoasterPager>> vector;
                for (uint i = 0; i < count; ++i)
                    vector.emplace_back(system.order({name}));
                return vector;
            };

            int i = 20;
            auto p = flood("burger", i);
            assert(system.getPendingOrders().size() == i);
            assert(ranges::all_of(p.begin(), p.end(), [&system, &i](auto &n) {
                n->wait();
                system.collectOrder(std::move(n));
                uint size = system.getPendingOrders().size();
                cout << size << "-" << --i << ", ";
                fflush(stdout);
                return size == i;
            }));
            system.shutdown();
            GOOD;
        });
    }

    if (which[3]) {
        cout << "============== DEMO ==============" << endl;
        std::invoke([] {
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
                std::cout << "OK\n";
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
                    std::cout << "OK\n";
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
                    std::cout << "OK\n";
                }
            });
            latch.count_down();
            shutdown_latch.wait();
            system.shutdown();
            bad_latch.count_down();
        });
    }
}

int main() {
    demo();
    return 0;
}
