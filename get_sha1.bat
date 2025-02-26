@echo off
echo Getting SHA-1 fingerprint for Firebase...
echo.

REM Set the path to the debug keystore
set KEYSTORE_PATH=%USERPROFILE%\.android\debug.keystore

REM Check if the keystore exists
if not exist "%KEYSTORE_PATH%" (
    echo Error: Debug keystore not found at %KEYSTORE_PATH%
    goto :end
)

REM Run keytool command
echo Running keytool command...
keytool -exportcert -alias androiddebugkey -keystore "%KEYSTORE_PATH%" -storepass android -keypass android | openssl sha1 -binary | openssl base64

echo.
echo If you don't see a SHA-1 fingerprint above, you may need to install OpenSSL.
echo Alternatively, you can use the Firebase CLI to get the SHA-1 fingerprint.

:end
pause 