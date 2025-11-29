# Moon Bound 🌙

**A minimalistic 2D web game made in Kotlin using Korge.**  
**Aim:** Fly your avatar to the moon while dodging asteroids.

---

## 📖 Описание / Description

**Russian:**  
Moon Bound — это минималистичная веб-игра, где игрок управляет реактивным ранцем, чтобы достичь Луны, уклоняясь от падающих астероидов. Цель — пройти уровень за ограниченное время и с ограниченным количеством жизней.

**English:**  
Moon Bound is a minimalistic web game where the player controls a jetpack to reach the moon while avoiding falling asteroids. The goal is to complete the level within a time limit and limited lives.

---

## ⏱ MVP Features

**Russian:**
- Игрок управляет реактивным ранцем (стрелки для движения)
- Падающие астероиды как препятствия
- 3 жизни
- Таймер на уровень
- Победа при достижении Луны

**English:**
- Player controls a jetpack (arrow keys)
- Falling asteroids as obstacles
- 3 lives
- Timer per level
- Win by reaching the moon


---

## 🖥 How to Run / Как запустить

1. Clone the repository / Склонируйте репозиторий::
```bash
git clone https://github.com/tionge45/moon_bound.git
cd MoonBound
````

2. Upgrade Yarn lock (first time only) / Обновите Yarn lock (только при первом запуске):
```bash
` .\gradlew kotlinUpgradeYarnLock`
```

3. Run web version / Запустите веб-версию:
```bash
`.\gradlew jsBrowserDevelopmentRun
```

### Upcoming project Structure
```bash
MoonBound/
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── src/
│   ├── commonMain/
│   │   └── kotlin/
│   │       ├── Main.kt
│   │       ├── Player.kt
│   │       ├── Obstacle.kt
│   │       └── Utils.kt
│   └── jsMain/
│       └── resources/
│           ├── images/
│           └── sounds/
└── README.md

```
