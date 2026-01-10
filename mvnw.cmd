@echo off
setlocal

set MAVEN_VERSION=3.9.6
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%
set MAVEN_ZIP=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%.zip
set MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip

if exist "%MAVEN_HOME%\bin\mvn.cmd" goto run

echo Maven not found. Downloading Maven %MAVEN_VERSION%...

if not exist "%USERPROFILE%\.m2\wrapper\dists" mkdir "%USERPROFILE%\.m2\wrapper\dists"

echo Downloading from: %MAVEN_URL%
powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%'"

if not exist "%MAVEN_ZIP%" (
    echo Failed to download Maven
    exit /b 1
)

echo Extracting Maven...
powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Failed to extract Maven
    exit /b 1
)

echo Maven installed successfully!

:run
"%MAVEN_HOME%\bin\mvn.cmd" %*
