#!/bin/sh
#
# Gradle startup script for UN*X
#

# Attempt to set APP_HOME
PRG="$0"
while [ -h "$PRG" ] ; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=$(dirname "$PRG")"/$link"
    fi
done
APP_HOME=$(dirname "$PRG")

# Resolve to absolute path
APP_HOME=$(cd "$APP_HOME" && pwd)

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine JAVA_HOME
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD="java"
    which java > /dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found."
fi

die() {
    echo
    echo "$*"
    echo
    exit 1
}

# Check if gradle-wrapper.jar exists
if [ ! -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
    die "ERROR: gradle-wrapper.jar not found at $APP_HOME/gradle/wrapper/gradle-wrapper.jar

To fix this, either:
  1. Open the project in Android Studio (recommended — it auto-downloads everything)
  2. Run: gradle wrapper --gradle-version 8.7
  3. Or open this project in Android Studio once, then use ./gradlew"
fi

exec "$JAVACMD" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
