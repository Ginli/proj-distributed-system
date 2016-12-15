# Project2-Consistency-Test
Distributed System

How it works:

1. Multithread Client Tester

    Build on [proj1-kvstore-thrift](https://github.com/YuBPan/proj-distributed-system/tree/master/proj1-kvstore-thrift), now create multi threads as different clients to send bunch of requests to server, and save results to log files.

2. Parse Log Files

    Create LogParser class in util which can parse, then analyze multiple generated log files and return a list of OperationInterval objects.

    To start parsing log files, create new LogParser instance and call startParsingLogs(path, file number). The "path" argument is the directory which contains log files. The "file number" is the amount of log files.

    When there are error messages in log files, the above parse method will throw a ConnectionErrorException which indicates there might be error when getting results from server.

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
$ git clone https://github.com/YuBPan/proj-distributed-system.git
$ cd proj-distributed-system/proj2-consistency-test/
$ mvn package
[INFO] Scanning for projects...
[INFO]
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.907 s
[INFO] Finished at: 2016-12-14T22:14:53-05:00
[INFO] Final Memory: 25M/285M
[INFO] ------------------------------------------------------------------------
$ java -cp ./target/distributedsystem-project2-consistency-test-1.0-SNAPSHOT-jar-with-dependencies.jar  kvstore.KVStoreConsistencyTester -server 127.0.0.1:9090
2016-12-14 22:15:39:152 INFO  client_11:154 - cmdIndex=-1, cmd=set, timeType=start, key=iamakey16, val=0
...
2016-12-14 22:15:42:124 INFO  client_13:154 - cmdIndex=29, cmd=get, timeType=finish, key=iamakey16, val=666
Parsing logs...
Generating graph...
Checking atomicity...
Error code:	0
```

Reference:<br>
[1] [What consistency does your key-value store actually provide?](https://www.usenix.org/legacy/event/hotdep10/tech/full_papers/Anderson.pdf)
