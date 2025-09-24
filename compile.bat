@echo off
setlocal EnableDelayedExpansion

:: Configuration
set "PROJECT_ROOT=%CD%"
set "JAR_NAME=framework.jar"
set "FRAMEWORK_SRC=%PROJECT_ROOT%\main\java"
set "TARGET_LIB=%PROJECT_ROOT%\..\test\WEB-INF\lib"
set "BUILD_DIR=%PROJECT_ROOT%\build"
set "CLASSES_DIR=%BUILD_DIR%\classes"

:: Check if source directory exists
if not exist "%FRAMEWORK_SRC%" (
    echo Error: Source directory "%FRAMEWORK_SRC%" not found
    exit /b 1
)

:: Create temporary build directories
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"

echo === Cleaning previous builds ===
if exist "%BUILD_DIR%" rd /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"
mkdir "%CLASSES_DIR%"

echo === Compiling Java files ===
:: Create CLASSPATH
set "CLASSPATH="
for %%i in ("%TARGET_LIB%\*.jar") do (
    if "!CLASSPATH!"=="" (
        set "CLASSPATH=%%i"
    ) else (
        set "CLASSPATH=!CLASSPATH!;%%i"
    )
)

:: Compile Java files while preserving package structure
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
:: Create target lib directory if it doesn't exist
if not exist "%TARGET_LIB%" mkdir "%TARGET_LIB%"

:: Copy the JAR file
copy "%JAR_NAME%" "%TARGET_LIB%"
if errorlevel 1 (
    echo Error: Failed to copy JAR to "%TARGET_LIB%"
    exit /b 1
)

:: Clean up
del "%JAR_NAME%"

echo === Build and deployment completed ===
endlocal