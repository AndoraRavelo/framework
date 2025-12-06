@echo off
setlocal EnableDelayedExpansion

set "PROJECT_ROOT=%CD%"
set "JAR_NAME=jar"
set "FRAMEWORK_SRC=%PROJECT_ROOT%\main\java"
set "TARGET_LIB=%PROJECT_ROOT%\..\test\WEB-INF\lib"
set "BUILD_DIR=%PROJECT_ROOT%\build"
set "CLASSES_DIR=%BUILD_DIR%\classes"

if not exist "%FRAMEWORK_SRC%" (
    echo Error: Source directory "%FRAMEWORK_SRC%" not found
    exit /b 1
)

if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"

echo === Cleaning previous builds ===
if exist "%BUILD_DIR%" rd /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"
mkdir "%CLASSES_DIR%"

echo === Compiling Java files ===
set "CLASSPATH="
for %%i in ("%TARGET_LIB%\*.jar") do (
    if "!CLASSPATH!"=="" (
        set "CLASSPATH=%%i"
    ) else (
        set "CLASSPATH=!CLASSPATH!;%%i"
    )
)

dir /s /b "%FRAMEWORK_SRC%\*.java" > sources.txt
if not exist sources.txt (
    echo Error: No Java source files found in "%FRAMEWORK_SRC%"
    exit /b 1
)
javac -cp "%CLASSPATH%" -d "%CLASSES_DIR%" @sources.txt
if errorlevel 1 (
    echo Compilation error
    del sources.txt
    exit /b 1
)
del sources.txt

echo === Creating JAR file ===
cd "%CLASSES_DIR%"
jar -cvf "%PROJECT_ROOT%\%JAR_NAME%" .
cd "%PROJECT_ROOT%"

echo === Copying JAR to test/WEB-INF/lib ===
if not exist "%TARGET_LIB%" mkdir "%TARGET_LIB%"

copy "%JAR_NAME%" "%TARGET_LIB%"
if errorlevel 1 (
    echo Error: Failed to copy JAR to "%TARGET_LIB%"
    exit /b 1
)

del "%JAR_NAME%"

echo === Build and deployment completed ===
endlocal