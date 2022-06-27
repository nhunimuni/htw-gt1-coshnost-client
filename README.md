# Projekt 2: Echtzeit-Strategie
HTW Berlin - GT1 AI for Games and Interactive Systems SoSe2022

### Spielregeln

- Drei Spielparteien spielen gleichzeitig in "Echtzeit".
- Ziel des Spiels ist es, möglichst große Bereiche der Spielwelt durch "Überfahren" mit eigenen Bots mit der eigenen Farbe zu färben.
- Die Spielwelt enthält unveränderliche Gräben, die die Bewegungsfreiheit beschränken.
- Jede Spielpartei hat Kontrolle über drei Bots (nummeriert 0, 1, 2).
  - Bot 0 (einfarbig) hat die höchste Geschwindigkeit, erhöht eigenen Farbanteil
  - Bot 1 (gepunktet) ist am langsamsten, setzt eigenen Farbanteil hoch
  - Bot 2 (gestreift) kann sich über Gräben bewegen, löscht alle Farben inklusive der eigenen
- Die Geschwindigkeit der Bots ist festgelegt und kann nicht gebremst werden. Steuerung erfolgt ausschließlich durch Lenken (Richtungsvektor beliebiger Länge).
- Bots blockieren sich nicht gegenseitig, aber es kann die eigene Farbe und die anderer Parteien übermalt/gelöscht werden.
- Das Spiel endet mit Ablauf des Zeitlimits. Gewonnen hat die Partei mit der maximalen Punktzahl. Haben mehrere Parteien die maximale Punktzahl, endet das Spiel unentschieden.
