# Project2-Consistency-Test
Distributed System

How it works:

1. Multithread Client Tester

    Build on [proj1-kvstore-thrift](https://github.com/YuBPan/proj-distributed-system/tree/master/proj1-kvstore-thrift), now create multi threads as different clients to send bunch of requests to server, and save results to log files.

2. Parse Log Files

    Create [LogParser](https://github.com/YuBPan/proj-distributed-system/blob/master/proj2-consistency-test/src/main/java/util/LogParser.java) class in util which can parse, then analyze multiple generated log files and return a list of [OperationInterval](https://github.com/YuBPan/proj-distributed-system/blob/master/proj2-consistency-test/src/main/java/util/OperationInterval.java) objects.

    To start parsing log files, create new LogParser instance and call startParsingLogs(path, file number). The "path" argument is the directory which contains log files. The "file number" is the amount of log files.

    When there are error messages in log files, the above parse method will throw a [ConnectionErrorException](https://github.com/YuBPan/proj-distributed-system/blob/master/proj2-consistency-test/src/main/java/util/exceptions/ConnectionErrorException.java) which indicates there might be error when getting results from server.

3. Generate Graph

   Transfer the parsed log objects `OperationInterval` to a graph including time edges, data edges and hybrid edges, then return an adjacent list in form of `map`.

   Algorithms to generate edges:

   1. Generate time edges. Time complexity: *O(nlogn)*

      ```
      A := all intervals in increasing order of start time;
      B := all intervals in decreasing order of finish time;
      foreach (a ∈ A)
      	t := −∞;
      	foreach (b ∈ B such that b < a)
      		if (t < b’s finish time)
      			add edge b → a;
      			t := max(t, b’s start time);
      		else break;
      ```

   2. Generate data edges. Time complexity: *O(n<sup>2</sup>)*

      ```
      A := all intervals that is a read
      B := all intervals that is a write
      foreach (a ∈ A)
      	foreach(b ∈ B)
      		if b.value.equals(a.value)
      			add edge b → a
      ```

   3. Generate hybrid edges. Time complexity: *O(n<sup>3</sup>)*

      ```
      A := all intervals that is a read
      B := all intervals that is a write
      foreach (b ∈ B)
      	foreach(a ∈ A)
      		if there is a path from b to a
      			add edge b → a's dictating write
      ```

   Total time complexity: *O(nlogn)+O(n<sup>2</sup>)+O(n<sup>3</sup>)=O(n<sup>3</sup>)*

4. Check Atomicity

    The resulting graph above is a DAG iff the trace is atomic, so now use depth-first search to check if there exists a cycle in the graph. The test results would be:

    Exit Code | Meaning
    --- | ---
    0 | Test passed
    1 | There were consistency errors.
    2 | Test was inconclusive.

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

Reference:<br>
[1] [What consistency does your key-value store actually provide?](https://www.usenix.org/legacy/event/hotdep10/tech/full_papers/Anderson.pdf)
