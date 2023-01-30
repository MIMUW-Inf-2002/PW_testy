#include <iostream>
#include <cassert>
#include "system.hpp"

#define START(string) cout << (string); fflush(stdout)
#define EXCEPT(string) cerr << "caught " << (string) << " -> "
#define GOOD cout << "GOOD" << endl
#define BAD cout << "BAD" << endl; exit(1)

using namespace std;

namespace {
template<typename T, typename V>
bool checkType(const V *v) {
    return dynamic_cast<const T *>(v) != nullptr;
}

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
public:

    SpringRollMachine(): running(false) {}

    unique_ptr<Product> getProduct() override {
        if (!running) throw MachineNotWorking();
        wcount++;
        unique_lock<std::mutex> lock(mutex);
        cond.wait(lock, [this]() { return !queue.empty(); });
        wcount--;
        auto product = std::move(queue.front());
        queue.pop_front();
        return product;
    }

    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<SpringRoll>(product.get())) throw BadProductException();
        if (!running) throw MachineNotWorking();
        lock_guard<std::mutex> lock(mutex);
        queue.push_front((unique_ptr<SpringRoll> &&) (std::move(product)));
        cond.notify_one();
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

class ApplePieMachine : public Machine {
public:
    unique_ptr<Product> getProduct() override {
        this_thread::sleep_for(chrono::seconds(2));
        throw MachineFailure();
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<ApplePie>(product.get())) throw BadProductException();
    }
    void start() override {}
    void stop() override {}
};

void returns_on_failure() {
    START("WORKERS RETURN ON FAILURE: ");
    System system{
    {{"springRoll", shared_ptr<Machine>(new SpringRollMachine())},
     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
     {"applePie", shared_ptr<Machine>(new ApplePieMachine())}},
    10, 100};
    vector<string> failed1(50, "springRoll");
    failed1.emplace_back("iceCream");
    vector<string> failed2(50, "springRoll");
    failed1.emplace_back("applePie");
    auto p1 = system.order(failed1);
    auto p2 = system.order(failed1);
    auto p3 = system.order(vector<string>(100, "springRoll"));
    try { p1->wait(); } catch (FulfillmentFailure const & e) {
        EXCEPT("FulfillmentFailure");
    }
    try { p2->wait(); } catch (FulfillmentFailure const & e) {
        EXCEPT("FulfillmentFailure");
    }
    p3->wait();
    auto spring_rolls = system.collectOrder(std::move(p3));
    system.shutdown();
    assert(checkType<SpringRoll>(spring_rolls[99].get()));
    GOOD;
}

void returns_on_timeout() {
    START("WORKERS RETURN ON TIMEOUT: ");
    System system{
    {{"springRoll", shared_ptr<Machine>(new SpringRollMachine())},
     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())},
     {"applePie", shared_ptr<Machine>(new ApplePieMachine())}},
    10, 100};
    vector<string> failed1(50, "springRoll");
    vector<string> failed2(50, "springRoll");
    auto p1 = system.order(failed1);
    auto p2 = system.order(failed1);
    p1->wait();
    p2->wait();
    this_thread::sleep_for(chrono::seconds(1));
    try {
        system.collectOrder(std::move(p1));
    } catch (OrderExpiredException const & e) {
        EXCEPT("OrderExpiredException");
    }
    try {
        system.collectOrder(std::move(p2));
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
void test() {
    returns_on_failure();
    returns_on_timeout();
}
}
