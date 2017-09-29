#!/bin/sh
mkdir -p build
find . -name "*.java" > sources.txt
javac -classpath lib/junit-jupiter-api-5.0.0.jar:lib/apiguardian-api-1.0.0.jar:lib/okio-1.13.0.jar:lib/okhttp-3.9.0.jar -d build @sources.txt
cp certs/DSTRootCAX3.pem build/io/readme/DSTRootCAX3.pem
mkdir -p package
jar cf package/io.readme-0.0.1.jar -C build .
java -jar lib/junit-platform-console-standalone-1.0.0.jar -classpath lib/junit-jupiter-api-5.0.0.jar:lib/junit-jupiter-engine-5.0.0.jar:lib/apiguardian-api-1.0.0.jar:lib/okio-1.13.0.jar:lib/okhttp-3.9.0.jar:package/io.readme-0.0.1.jar --scan-class-path
