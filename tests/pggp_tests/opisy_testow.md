# Opisy testów pggp

- Testy sprawdzające żywotność odczekują 10 milisekund między dwoma kolejnymi akcjami globalnie. Więc mogą zająć dużo czasu. 
  Ten czas można zmniejszyć edytując zmienną `SimulationWithBugCheck.timeOfWaitBetweenActionsWhenOrderMatters` w klasie Main,
- Do debugowania zaleca się włączenie `verbose = true` w klasie `Main`. Powinno wystarczyć. Przy czym jeśli nie jest sprawdzana żywotnośc,
  to nie ma 100% gwarancji, że kolejność wypisań się zgadza.
- Dokładne opisy są w odpowiednich plikach,