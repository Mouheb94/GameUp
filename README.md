# 📘 GameUp — Backend Java + Service Python de Recommandation

Système backend pour la gestion d’une boutique de jeux vidéo (Spring Boot) accompagné d’un service Python de recommandation (KNN).

---

#  Table des matières

1. Prérequis
2. Configuration de l’environnement
3. Lancement du projet
    - Base de données MySQL
    - Backend Spring Boot
    -  API Python de recommandation
4. 📚Documentation API
5.  Tests & rapports de couverture


---

# 1️ Prérequis

- **Java JDK 17+**
- **Maven**
- **Python 3.10+**
- **MySQL**
- **PowerShell / CMD**
- Éditeur de code : IntelliJ IDEA / VS Code

---

# 2️ ⚙️ Configuration de l’environnement

## ✏️ Éditeur conseillé
- **IntelliJ IDEA 2024.3.5** pour le backend Java
- **VS Code** pour le service Python

## 💻 Installation

### 1. Installation Java
```bash
java -version
```

### 2. Installation Maven
```bash
mvn --version
```

### 3. Installation MySQL
Créer une nouvelle connexion MySQL :
```
hostname : localhost  
port     : 3306  
user     : root  
password : root
```

---

# 3️ Lancement du projet

## 🗄️ Configuration MySQL
Dans MySQL :

```sql
CREATE DATABASE GameUp;
```

---

## 🔧 Lancement du backend Spring Boot

Dans le dossier **gamesUP/** :

```bash
mvn spring-boot:run
```

Backend accessible sur :

👉 http://localhost:8080

---

# 🚀 Démarrer le service Python (`CodeApiPython/`)

Le moteur de recommandation s’appuie sur une API **FastAPI** locale.

## 1️ Installer les dépendances Python

Ouvrir un terminal dans `CodeApiPython/` :

```bash
cd CodeApiPython
pip install -r requirements.txt
```

---

## 2️ Lancer l’API FastAPI

Toujours dans `CodeApiPython/` :

```bash
uvicorn main:app --reload
```
---
# 3 Documentation API (backend Java)

Swagger UI est disponible sur :

👉 http://localhost:8080/swagger-ui/index.html

---

# 5️ Tests & rapport de couverture

### Lancer les tests :
```bash
mvn clean verify
```

### Rapport JaCoCo :
Ouvrir :

```
gamesUP/target/site/jacoco/index.html
```

---


