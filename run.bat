@echo off

rem Compile the Java source code
javac -d bin -cp src src\Controle\*.java src\Controle\SetObj\*.java src\Modele\*.java src\Modele\Enregistrer\*.java src\Modele\Signalling\*.java src\Modele\Vehicle\*.java src\Vue\*.java


if ERRORLEVEL 1 (
    echo Compilation failed! Exiting...
    exit /b 1
)

rem Execute the compiled program
java -cp "bin;." Main

pause