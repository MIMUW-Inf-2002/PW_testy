package cp2022.tests.kwasow;

public class KwasowMain {

  public static void main(String[] args) {
    // Ustaw tę wartość na true, jeśli chcesz otrzymywać takie same logi jak
    // w oficjalnym przykładzie
    boolean verbose = false;

    // Ten test ma dwóch robotników, którzy na zmianę używają dwóch narzędzi
    // i przesiadają się między sobą
    KwasowPairCycleTest.run(verbose);

    // Ten test ma czterech robotników i trzy stanowiska. Trzech z nich przesiada
    // się w cyklu, a czwarty (Seba) próbuje im się wbić w ten cykl w pewnym
    // momencie (dokładniej to wtedy, kiedy wykonają jedno pełne przesiadanie
    // w cyklu chyba, aczkolwiek tu oczywiści może się zdarzyć jakiś dziwny
    // przeplot, który sprawi, że Seba przyjdzie dopiero, jak cała trójka
    // sobie już pójdzie).
    KwasowBigCycleTest.run(verbose);
  }

}
