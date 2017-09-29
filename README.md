# api-java

This is a language-specific API client for [ReadMe Build](https://readme.build). It's based on the [api spec](https://github.com/readmeio/api-spec).

This implementation works around Let's Encrypt issues (in Java releases earlier than 1.8.u101 and with minimal dependancies, so it should fit into any project.

## Building

This library can be built entirely from the comamndline using only the standard JDK. A helper script called `build-sx.sh` in included to run these build steps (and execute the JUnit test) however to build just the client API you can follow these steps:

### On OSX (or any linux like platform)

    mkdir -p build
    find . -name "src/*.java" > sources.txt
    javac -classpath lib/okio-1.13.0.jar:lib/okhttp-3.9.0.jar -d build @sources.txt
    cp certs/DSTRootCAX3.pem build/io/readme/DSTRootCAX3.pem
    mkdir -p package
    jar cf package/io.readme-0.0.1.jar -C build .

This will produce a file called `io.readme-0.0.1.jar` which when added to your project (along with `lib/okio-1.13.0.jar` and `lib/okhttp-3.9.0.jar`) will let you call the ReadMe Build API.

## Installation

Add `io.readme-0.0.1.jar`, `okio-1.13.0.jar` and `okhttp-3.9.0.jar` to your class path.

## Usage

```java
import io.readme.Api;
import io.readme.ApiResponse;
```

Then in your java funciton:

```java
Api api = new Api(API_KEY);
ApiResponse response = api.run("math", "multiply", "{\"numbers\": [10,20,30]}");

assert response.statusCode == 200;
assert response.body.equals("6000");
```

## Running Test:

If you have compiled the source files with the `test` source files you can run the JUnit tests with the following command:

    java -jar lib/junit-platform-console-standalone-1.0.0.jar -classpath lib/junit-jupiter-api-5.0.0.jar:lib/junit-jupiter-engine-5.0.0.jar:lib/apiguardian-api-1.0.0.jar:lib/okio-1.13.0.jar:lib/okhttp-3.9.0.jar:package/io.readme-0.0.1.jar --scan-class-path
