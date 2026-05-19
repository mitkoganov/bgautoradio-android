@rem Gradle startup script for Windows

@rem Set local scope for the variables with windows NT shell
@if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Check if gradle-wrapper.jar exists
if not exist "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" (
    echo.
    echo ERROR: gradle-wrapper.jar not found.
    echo.
    echo To fix this, either:
    echo   1. Open the project in Android Studio ^(recommended^)
    echo   2. Run: gradle wrapper --gradle-version 8.7
    echo.
    exit /b 1
)

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo ERROR: JAVA_HOME is not set and no 'java' command could be found.
exit /b 1

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
exit /b 1

:execute
"%JAVA_EXE%" -classpath "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*

:end
@if "%OS%"=="Windows_NT" endlocal
