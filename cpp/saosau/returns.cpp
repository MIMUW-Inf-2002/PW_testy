#include <iostream>
#include <cassert>
#include "system.hpp"

#define START(string) cerr << (string)
#define EXCEPT(string) cerr << "caught " << (string) << " -> "
#define GOOD cerr << "GOOD" << endl
#define BAD cerr << "BAD" << endl; exit(1)

using namespace std;

namespace {
template<typename T, typename V>
bool checkType(const V *v) {
    return dynamic_cast<const T *>(v) != nullptr;
}

class UnsafeGetProductException: public std::exception {};
class UnsafeReturnProductException: public std::exception {};

class SpringRoll : public Product {};
class IceCream : public Product {};
class ApplePie: public Product {};

class SpringRollMachine : public Machine {
    std::mutex mutex;
    condition_variable cond;
    atomic<int> wcount;
    deque<unique_ptr<SpringRoll>> queue;
    atomic<bool> running;
    int const COUNT = 100;
    atomic<bool> vacant1, vacant2;
public:

    SpringRollMachine(): running(), vacant1(), vacant2() {}

    unique_ptr<Product> getProduct() override {
        if (vacant1) throw UnsafeGetProductException();
        if (!running) throw MachineNotWorking();
        vacant1 = true;
        wcount++;
        unique_lock<std::mutex> lock(mutex);
        cond.wait(lock, [this]() { return !queue.empty(); });
        wcount--;
        auto product = std::move(queue.front());
        queue.pop_front();
        vacant1 = false;
        return product;
    }

    void returnProduct(unique_ptr<Product> product) override {
        if (vacant2) throw UnsafeReturnProductException();
        if (!checkType<SpringRoll>(product.get())) throw BadProductException();
        if (!running) throw MachineNotWorking();
        vacant2 = true;
        lock_guard<std::mutex> lock(mutex);
        queue.push_front((unique_ptr<SpringRoll> &&) (std::move(product)));
        cond.notify_one();
        vacant2 = false;
    }

    void start() override {
        running = true;
        for (int i = 0; i < COUNT; ++i)
            queue.emplace_back(make_unique<SpringRoll>());
    }

    void stop() override {
        running = false;
    }
};

class IceCreamMachine : public Machine {
public:
    unique_ptr<Product> getProduct() override {
        this_thread::sleep_for(chrono::seconds(2));
        throw MachineFailure();
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<IceCream>(product.get())) throw BadProductException();
    }
    void start() override {}
    void stop() override {}
};

// There's only one apple pie. Lucky!
class ApplePieMachine : public Machine {
bool taken;
public:
    bool returned;
    ApplePieMachine(): taken(), returned() {}
    unique_ptr<Product> getProduct() override {
        !taken ? taken = true : throw MachineFailure();
        return make_unique<ApplePie>();
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<ApplePie>(product.get())) throw BadProductException();
        returned = true;
    }
    void start() override {}
    void stop() override {}
};

auto flood(System & system, auto const & order, uint count) {
    vector<unique_ptr<CoasterPager>> pagers;
    for (uint i = 0; i < count; ++i)
        try {
            pagers.emplace_back(system.order(order));
        } catch (BadOrderException const &) { EXCEPT("BadOrderException"); }
    return pagers;
}

void returns_on_failure() {
    START("WORKERS RETURN ON FAILURE: ");
    auto apple_pie_machine = make_shared<ApplePieMachine>();
    System system{
    {{"springRoll", shared_ptr<Machine>(new SpringRollMachine())},
     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
     {"applePie", apple_pie_machine}}, 10, 100};
    vector<string> failed1(20, "springRoll");
    failed1.emplace_back("iceCream");
    vector<string> failed2(20, "springRoll");
    failed2.emplace_back("applePie");
    failed2.emplace_back("applePie");
    auto p1 = flood(system, failed1, 5);
    auto p2 = flood(system, failed2, 5);
    auto p3 = system.order(vector<string>(100, "springRoll"));
    for (auto & p: p1)
        try { p->wait(); } catch (FulfillmentFailure const &e) {
            EXCEPT("FulfillmentFailure");
        }
    for (auto & p: p2)
        try { p->wait(); } catch (FulfillmentFailure const &e) {
            EXCEPT("FulfillmentFailure");
        }
    p3->wait();
    auto spring_rolls = system.collectOrder(std::move(p3));
    system.shutdown();
    assert(checkType<SpringRoll>(spring_rolls[99].get()));
    assert(apple_pie_machine->returned);
    GOOD;
}

void returns_on_timeout() {
    START("WORKERS RETURN ON TIMEOUT: ");
    System system{
    {{"springRoll", shared_ptr<Machine>(new SpringRollMachine())},
     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
     {"applePie", shared_ptr<Machine>(new ApplePieMachine())}},
    10, 100};
    auto pagers = flood(system, vector<string>(20, "springRoll"), 10);
    for (auto & p: pagers)
        p->wait();
    this_thread::sleep_for(chrono::seconds(1));
    for (auto & p: pagers)
        try {
            system.collectOrder(std::move(p));
        } catch (OrderExpiredException const & e) {
            EXCEPT("OrderExpiredException");
        }
    auto p3 = system.order(vector<string>(100, "springRoll"));
    p3->wait();
    auto spring_rolls = system.collectOrder(std::move(p3));
    system.shutdown();
    assert(checkType<SpringRoll>(spring_rolls[99].get()));
    GOOD;
}
}
namespace saosau {
void returns_test() {
    cerr << '\n';
    cout << "Returns Tests (takes 3 seconds)" << endl;
    returns_on_failure();
    returns_on_timeout();
}
}
