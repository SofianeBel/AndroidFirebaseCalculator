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

## Bibliothèques utilisées

- AndroidX
- Firebase Realtime Database
- Material Design Components

## Licence

Ce projet est sous licence MIT.