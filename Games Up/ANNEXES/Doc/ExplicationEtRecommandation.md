# Mise en place du système de recommandation dans GameUp 

## 1. Fichiers référencés et leur rôle

| Fichier / Dossier                                         | Rôle                                                                                      |
|-----------------------------------------------------------|-------------------------------------------------------------------------------------------|
| `gamesUP/`                                                | Application Spring Boot (Java, Maven).                                                    |
| `gamesUP/pom.xml`                                         | Gestion des dépendances, plugins et build Maven.                                          |
| `gamesUP/src/main/java/com/`                              | Code source Java (entités, repositories, services, controllers, DTO, config).            |
| `gamesUP/src/test/`                                       | Tests unitaires Java + ressources de test.                                                |
| `gamesUP/target/`                                         | Artefacts buildés (`gamesUP-0.0.1-SNAPSHOT.jar`) et rapports JaCoCo.                     |
| `CodeApiPython/`                                          | Service Python de recommandation (FastAPI + modèle KNN).                                 |
| `CodeApiPython/recommendation.py`                         | Logique principale du moteur de recommandation.                                           |
| `CodeApiPython/main.py`                                   | API REST FastAPI / point d’entrée du service Python.                                      |
| `CodeApiPython/data_loader.py`, `CodeApiPython/models.py` | Chargement des données et outils du modèle.                                               |
| `CodeApiPython/training_data.csv`                         | Données d’entraînement pour le modèle KNN.                                               |
| `Games Up/ANNEXES/Doc`                                    | Documents d’explication et schémas d’intégration.                                         |

---

## 2. Explication du fonctionnement

1. Collecte et gestion des données
    - Les entités Java (dans `gamesUP/src/main/java/com/...`) modélisent la BD.
    - Les repositories Spring Data exposent les interactions (achats, avis).
    - Les interactions utilisées pour la reco peuvent être exportées en CSV/JSON ou lues directement depuis la BD.

2. Entraînement et service Python
    - `CodeApiPython/recommendation.py` utilise `training_data.csv` et les utilitaires (`data_loader.py`, `models.py`) pour entraîner/charger le modèle KNN.
    - `CodeApiPython/main.py` peut exposer une API (FastAPI/Flask) ou fournir une interface CLI.

3. Appel du moteur de recommandation depuis Java
    - Option recommandée : exposer `CodeApiPython` via HTTP (FastAPI + Uvicorn) et appeler depuis Java via `RestTemplate` ou `WebClient`.
    - Alternative simple : exécuter `python main.py --userId X` en subprocess (moins scalable).
    - Option asynchrone : message queue (RabbitMQ/Kafka) si besoin de découplage fort.

4. Retour des recommandations
    - Le service Python renvoie une liste de `game_id`.
    - Java récupère les détails via `GameRepository` et renvoie des DTOs via un endpoint REST (`/api/recommendations/{userId}`).

---

## 3. Bonnes et Points d'amélioration 

### Bonnes pratiques
- Architecture en couches
- API Python séparée du backend
- Appels REST → bon découplage
- Tests automatisés + JaCoCo
- Documentation Swagger
- Service de recommandation isolé
- Utilisation de DTO

### Points d'amélioration 
- API Python lancée manuellement (pas automatisée)
- Gestion des erreurs réseau encore minimale
- Pas de caching pour la recommandation
- Pas de Docker (peut limiter la reproduction exacte en production)

---

## 4. Respect des principes SOLID

| Principe | Status | Explication |
|----------|--------|-------------|
| **S — Single Responsibility** | ✅ | Couches séparées : Controller / Service / Repository. |
| **O — Open/Closed** | 🟡 | Système extensible, mais amélioration possible. |
| **L — Liskov** | ✅ | Contrats d'interfaces respectés. |
| **I — Interface Segregation** | ✅ | Services spécialisés. |
| **D — Dependency Inversion** | 🟡 | Utilisation d’une interface `RecommendationService`. |

---

# 5.  Explication du système de recommandation

## 🔄 1. Collecte et gestion des données
- Le backend stocke les jeux, achats, avis, catégories…
- Les données utiles à la recommandation :  
  ✔ historique d’achat  
  ✔ notes / avis  
  ✔ similarité entre jeux

##  2. Service Python (KNN)
- Le fichier `training_data.csv` contient les interactions utilisateur-jeu.
- `recommendation.py` entraîne un modèle KNN basé sur :
    - similarité entre utilisateurs
    - similarité de profils de jeux

##  3. Communication entre Java et Python
✔ Via HTTP (FastAPI)  
✔ Implémenté dans Java via `RecommendationServiceHttpImpl`



# Résumé

Ce projet met en place :
✔ un backend Spring Boot structuré  
✔ une API Python FastAPI pour la recommandation  
✔ communication REST Java ↔ Python  
✔ respect global des règles SOLID  
✔ rapport de couverture JaCoCo  
✔ documentation claire (ce fichier)

 