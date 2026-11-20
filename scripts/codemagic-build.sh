#!/usr/bin/env bash
set -euo pipefail

GRADLE_VERSION="${GRADLE_VERSION:-8.11.1}"
GRADLE_HOME_DIR="${HOME}/.gradle/distributions/gradle-${GRADLE_VERSION}"
GRADLE_BIN="${GRADLE_HOME_DIR}/bin/gradle"
DIST_DIR="${HOME}/.gradle/distributions"
ZIP_PATH="${DIST_DIR}/gradle-${GRADLE_VERSION}-bin.zip"

mkdir -p "${DIST_DIR}"

if [[ ! -x "${GRADLE_BIN}" ]]; then
  echo "Téléchargement de Gradle ${GRADLE_VERSION}..."
  curl --fail --location --retry 3 \
    "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    --output "${ZIP_PATH}"
  unzip -q -o "${ZIP_PATH}" -d "${DIST_DIR}"
fi

"${GRADLE_BIN}" --version
"${GRADLE_BIN}" --no-daemon --stacktrace clean :app:assembleDebug

APK="app/build/outputs/apk/debug/app-debug.apk"
if [[ ! -s "${APK}" ]]; then
  echo "ERREUR : l'APK attendu n'a pas été généré : ${APK}" >&2
  exit 1
fi

echo "Compilation terminée : ${APK}"
