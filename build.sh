#!/bin/bash
export JAVA_HOME=/home/z/jdk8u412-b08
export MAVEN_HOME=/home/z/apache-maven-3.6.3
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH

cd /home/z/my-project/Stock-Master/inventory-backend

echo "=== Java Version ==="
java -version

echo ""
echo "=== Maven Version ==="
mvn -version

echo ""
echo "=== Starting Maven Build ==="
mvn clean compile -DskipTests -q

if [ $? -eq 0 ]; then
    echo "BUILD SUCCESS"
else
    echo "BUILD FAILED"
fi
