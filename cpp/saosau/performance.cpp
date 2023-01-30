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
        burgersMade = 100;
    }
    void stop() override {}
};

class IceCreamMachine : public Machine {
public:
    unique_ptr<Product> getProduct() override {
        this_thread::sleep_for(chrono::milliseconds(500));
        throw MachineFailure();
    }
    void returnProduct(unique_ptr<Product> product) override {
        if (!checkType<IceCream>(product.get())) throw BadProductException();
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

void performance() {
    bool const check_reports = true;
    std::vector<string> failed(CHAR_MAX, "burger");
    failed.emplace_back("iceCream");
    set_expected(
        {20, vector<string>(5, "burger")},
        {failed},
        {}, true
    );
    START("PERFORMANCE: ");
    System system{
    {{"burger", shared_ptr<Machine>(new BurgerMachine())},
     {"iceCream", shared_ptr<Machine>(new IceCreamMachine())}},
     10, 100};
    auto failed_pager = system.order(failed);
    auto pagers = flood(system, vector<string>(5, "burger"), 20);
    try {
        failed_pager->wait();
    } catch (FulfillmentFailure const &) {
        EXCEPT("FulfillmentFailure");
    }
    assert(ranges::all_of(pagers.begin(), pagers.end(), [&system] (auto & p) {
        p->wait();
        return checkType<Burger>(system.collectOrder(std::move(p)).at(0).get());
    }));
    REPORT(system.shutdown());
    GOOD;
}
}

namespace saosau {
    void performance_test() {
        cerr << '\n';
        cout << "Performance Tests (takes a second)" << endl;
        performance();
    }
}
