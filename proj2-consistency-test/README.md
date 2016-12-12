# Proj2-Consistency-Test
Distributed System

How it works:

1. Multithread Client Tester

    Built on [proj1-kvstore-thrift](https://github.com/YuBPan/proj-distributed-system/tree/master/proj1-kvstore-thrift), now create multi threads as different clients to send bunch of requests to server, and save results to log files.

2. Parse Log Files

    Created LogParser class in util which can parse, analyze multiple generated log files and return a list of OperationInterval objects. 
    
    To start parsing log files, create new LogParser instance and call startParsingLogs(path, file number). "path" argument is the directory which contains log files. "file number" is the amount of log files.
    
    When there are error messages in log files, above parse method will throw a ConnectionErrorException which indicates there might be error when getting results from server.

3. Generate Graph

   Transfer the parsed log objects `OperationInterval` to a graph including time edges, data edges and hybrid edges, then return an adjacent list in form of `map`.

   Algorithms to generate edges:

   1. Generate time edges. From the <a href="https://www.usenix.org/legacy/event/hotdep10/tech/full_papers/Anderson.pdf">paper</a>. Time complexity: *O(nlogn)*

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

   3. Generate hybrid edges. From the <a href="https://www.usenix.org/legacy/event/hotdep10/tech/full_papers/Anderson.pdf">paper</a>. Time complexity: *O(n<sup>3</sup>)*

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

    how...

Compilers:
- Java 8

Run Instructions:
```sh
$ ./KVStoreConsistencyTester -server 127.0.0.1:9090
```
