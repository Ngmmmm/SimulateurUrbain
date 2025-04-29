
```markdown
# 🚦 Simulateur de Gestion de Trafic Urbain

Projet de L2 Programmation 2024–2025 — Université

## 📌 Description

Ce projet a pour but de simuler le trafic urbain d'une ville fictive à l'aide d'une **application Java interactive**. Il comprend la modélisation d'intersections, routes, feux de signalisation, et véhicules se déplaçant selon des règles réalistes. Le simulateur permet à l'utilisateur d'interagir avec la carte, d'observer le trafic, de consulter des statistiques, et d'optimiser la circulation grâce à des algorithmes intelligents.

## 🎯 Objectifs pédagogiques

- Utilisation de l'architecture **Modèle-Vue-Contrôleur (MVC)**.
- Conception d’une **interface graphique avec Swing**.
- Implémentation de **règles de trafic réalistes**.
- Collecte et affichage de **statistiques en temps réel**.
- Introduction aux **algorithmes d’optimisation heuristiques**.
- Travail collaboratif avec **Git/GitLab**.

## 🛠️ Fonctionnalités principales

### 🗺️ Carte Interactive
- Affichage de routes, intersections, feux de signalisation.
- Zoom/dézoom possible.
- Routes de tailles variées.

### 🚗 Simulation de trafic
- Véhicules variés : voitures, bus, camions.
- Différences de vitesse et de taille.
- Gestion de la priorité et des feux aux intersections.

### 🚦 Gestion des feux de signalisation
- Ajout et contrôle manuel des cycles.
- Interaction utilisateur via clics.

### 📊 Statistiques en temps réel
- Temps moyen de trajet.
- Temps de parcours par route.
- Embouteillages.
- Estimation de la consommation de carburant.

### 🧠 Extensions (optionnelles)
- Algorithmes d’optimisation (génétique, tabou, etc.).
- Sauvegarde/chargement de la simulation.

## 🧱 Architecture du projet

```
C:.
├── .vscode                 # Configuration de l’environnement de développement
├── bin                    # Fichiers compilés
│   ├── Controle
│   │   └── SetObj
│   ├── Modele
│   │   ├── Enregistrer
│   │   ├── Signalling
│   │   └── Vehicle
│   └── Vue
├── img                    # Images utilisées dans l’interface
├── Save                   # Fichiers de sauvegarde de simulation
└── src                    # Code source du projet
    ├── Controle           # Contrôleurs du projet
    │   └── SetObj
    ├── Modele             # Données et logique métier
    │   ├── Enregistrer
    │   ├── Signalling
    │   └── Vehicle
    └── Vue                # Interface graphique avec Swing
```

## 💻 Technologies utilisées

- **Java 17+**
- **Swing (javax.swing)** pour l’interface graphique
- **Git / GitLab** pour la collaboration
- **Timer** Swing pour l'animation
- **OOP et MVC** pour la structuration du code

## 🚀 Lancement du projet

1. Cloner le projet :
   ```bash
   git clone <url_du_dépôt>
   ```

2. Compiler le projet :
   ```bash
   javac -d bin src/**/*.java
   ```

3. Lancer l'application :
   ```bash
   java -cp bin Vue.Main
   ```

## 👥 Équipe projet

- 👤 [Nom 1] — Gestion de la carte et affichage
- 👤 [Nom 2] — Logique de simulation des véhicules
- 👤 [Nom 3] — Statistiques et optimisation
- 👤 [Nom 4] — Interface utilisateur et interactions
- 🧑‍🏫 Superviseur : Costas Bouyioukos

## 📅 Planification

- **Semaines 1–2** : Répartition des rôles, plan de projet
- **Semaine 6** : Version fonctionnelle minimale
- **Semaines 7+** : Optimisation, extensions, interface avancée

## 📂 Licence

Projet académique — Utilisation libre à des fins pédagogiques uniquement.
```

Souhaites-tu aussi un exemple de `gitignore` adapté pour ce projet Java ?