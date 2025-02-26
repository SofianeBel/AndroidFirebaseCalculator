# Calculatrice Android connectée à Firebase

Cette application est une calculatrice Android connectée à une base de données Firebase en temps réel. Elle permet d'effectuer des opérations mathématiques de base et de sauvegarder l'historique des calculs dans Firebase.

## Fonctionnalités

- Opérations mathématiques de base (addition, soustraction, multiplication, division)
- Sauvegarde de l'historique des calculs dans Firebase
- Synchronisation en temps réel entre les appareils
- Interface utilisateur intuitive

## Prérequis

- Android Studio
- Android SDK (API 24 minimum - Android 7.0)
- Compte Firebase

## Configuration

1. Clonez ce dépôt
2. Ouvrez le projet dans Android Studio
3. Connectez le projet à votre propre projet Firebase:
   - Créez un projet dans la [console Firebase](https://console.firebase.google.com/)
   - Ajoutez une application Android et suivez les instructions pour télécharger le fichier `google-services.json`
   - Placez le fichier `google-services.json` dans le répertoire `app/`

## Configuration de Firebase Realtime Database

Pour configurer la base de données Firebase Realtime Database pour cette application, suivez ces étapes :

1. Connectez-vous à la [console Firebase](https://console.firebase.google.com/).
2. Sélectionnez votre projet (androidcalculator-585e1).
3. Dans le menu de gauche, cliquez sur "Realtime Database".
4. Cliquez sur "Créer une base de données" si ce n'est pas déjà fait.
5. Choisissez l'emplacement de votre base de données (par défaut ou le plus proche de vos utilisateurs).
6. Commencez en mode test, puis passez aux règles de sécurité ci-dessous.

### Règles de sécurité

Pour sécuriser votre base de données, copiez les règles suivantes dans l'onglet "Règles" de votre Realtime Database :

```json
{
  "rules": {
    "calculations": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

Ces règles garantissent que :
- Chaque utilisateur ne peut lire et écrire que ses propres calculs
- Les calculs sont organisés par ID utilisateur
- Personne ne peut accéder aux calculs des autres utilisateurs

### Structure de la base de données

La structure de la base de données est organisée comme suit :

```
calculations/
  ├── user_id_1/
  │   ├── calculation_id_1/
  │   │   ├── expression: "2+2"
  │   │   ├── result: "4"
  │   │   ├── timestamp: 1645678912345
  │   │   ├── id: "calculation_id_1"
  │   │   └── userId: "user_id_1"
  │   └── calculation_id_2/
  │       └── ...
  └── user_id_2/
      └── ...
```

Cette structure permet de :
- Séparer les calculs par utilisateur
- Faciliter les requêtes pour récupérer uniquement les calculs d'un utilisateur spécifique
- Appliquer des règles de sécurité granulaires

## Bibliothèques utilisées

- AndroidX
- Firebase Realtime Database
- Material Design Components

## Licence

Ce projet est sous licence MIT.