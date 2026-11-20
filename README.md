# MÉDIAKI — préparation Codemagic

Cette archive contient le projet Android MÉDIAKI préparé pour une compilation cloud gratuite/à quota gratuit sur Codemagic.

## Configuration ajoutée

- `codemagic.yaml` : workflow Codemagic `mediaki-android-debug`.
- `scripts/codemagic-build.sh` : installe Gradle 8.11.1 dans le cache utilisateur si nécessaire, puis compile l'APK Debug.
- Compilation avec JDK 17, Android SDK Platform 35 et Build Tools 35.0.0.
- Artefact attendu : `app/build/outputs/apk/debug/app-debug.apk`.

Le workflow ne nécessite ni compte de signature ni secret : l'APK Debug est signé automatiquement avec la clé de débogage générée par Android Gradle Plugin. Il est destiné aux essais et à l'installation manuelle, pas à une publication Google Play.

## Importation dans Codemagic

1. Décompresser cette archive.
2. Créer un dépôt GitHub privé ou public et y téléverser le contenu du dossier `MediakiAndroid_Codemagic` (le fichier `codemagic.yaml` doit être à la racine du dépôt).
3. Dans Codemagic, ajouter le dépôt et choisir le workflow `mediaki-android-debug`.
4. Démarrer le build.
5. À la fin, récupérer `app-debug.apk` dans les artefacts du build.

Si Codemagic ne détecte pas automatiquement le workflow, vérifier que `codemagic.yaml` se trouve bien à la racine du dépôt puis relancer l'analyse du dépôt.

## Limites connues du code applicatif

Cette préparation concerne la compilation et ne transforme pas la base de code en produit final. La bibliothèque vidéo est actuellement conservée en mémoire et ne survit pas à la fermeture de l'application. L'écran « Espace privé » est informatif : il ne chiffre, ne déplace ni ne protège les vidéos. La prise en charge des formats dépend des codecs disponibles sur l'appareil.

## Compatibilité de compilation

Le projet utilise Android Gradle Plugin 8.9.2, Kotlin 2.1.20, compileSdk 35 et cible Android 15 (API 35). Le script fixe Gradle 8.11.1 et JDK 17 pour fournir un environnement reproductible. La compilation réelle doit encore être exécutée sur Codemagic : aucun APK n'est inclus dans cette archive.
