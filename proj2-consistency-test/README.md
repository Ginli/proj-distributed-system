# Proj2-Consistency-Test
Distributed System

How it works:

1. Multithread Client Tester

    Built on [proj1-kvstore-thrift](https://github.com/YuBPan/proj-distributed-system/tree/master/proj1-kvstore-thrift), now create multi threads as different clients to send bunch of requests to server, and save results to log files.

2. Parse Log Files

    how...

3. Generate Graph

    how...

4. Check Atomicity

    how...

OS:
- macOS 10.12.1

Language:
- Java 8

Compilers:
- JDK 8

Maven Dependencies:
```
<dependencies>
    <dependency>
        <groupId>org.apache.thrift</groupId>
        <artifactId>libthrift</artifactId>
        <version>0.9.3</version>
    </dependency>
    <dependency>
        <groupId>commons-cli</groupId>
        <artifactId>commons-cli</artifactId>
        <version>1.3</version>
    </dependency>

    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.21</version>
    </dependency>
</dependencies>
```

Run Instructions:
```sh
$ ./KVStoreConsistencyTester -server 127.0.0.1:9090
```
