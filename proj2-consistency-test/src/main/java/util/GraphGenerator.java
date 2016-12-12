package main.java.util;

import java.util.*;

/**
 * Created by raylee on 12/12/16.
 */
public class GraphGenerator {

    public static Map<OperationInterval, HashSet<OperationInterval>> generateGraph(List<OperationInterval> nodes) {
        Map<OperationInterval, HashSet<OperationInterval>> map = new HashMap<>();

        List<OperationInterval> increase = new ArrayList<>(nodes);
        List<OperationInterval> decrease = new ArrayList<>(nodes);

        Collections.sort(increase, (o1, o2) -> o1.getStartTime() - o2.getStartTime() < 0 ? 1 : 0);

        Collections.sort(decrease, (o1, o2) -> o1.getEndTime() - o2.getEndTime() > 0 ? 1 : 0);

        Map<OperationInterval, OperationInterval> successors = new HashMap<>();

        // add minimum number of time edges
        for (OperationInterval a : increase) {
            long t = Long.MIN_VALUE;
            for (OperationInterval b : decrease) {
                if (!map.containsKey(b)) {
                    map.put(b, new HashSet<>());
                }
                if (b.getEndTime() < a.getStartTime()) {
                    if (t < b.getEndTime()) {
//                        System.out.printf("~~~~Add Edge~~~~%n[%s, %s, %d - %d] -> [%s, %s, %d - %d]%n", b.operation, b.getValue(), b.getStartTime(), b.getEndTime(), a.operation, a.getValue(), a.getStartTime(), a.getEndTime());
                        successors.put(a, b);
                        map.get(b).add(a);
                        t = Long.max(t, b.getStartTime());
                    }
                    else {
                        break;
                    }
                }
            }
        }

//        System.out.printf("-------Time edges-------%n");
//        print(map);

        // add data edges
        for (OperationInterval o1 : nodes) {
            if (o1.operation.equals("get")) {
                for (OperationInterval o2 : nodes) {
                    if (o2.operation.equals("set") && o1.getValue().equals(o2.getValue())) {
                        successors.put(o1, o2);
                        map.get(o2).add(o1);
                    }
                }
            }
        }

//        System.out.printf("-------Data edges-------%n");
//        print(map);

        // add hybrid edges
        for (OperationInterval write : nodes) {
            // This is a write(set) operation
//            System.out.printf("1 %s, %s %n", write.operation, write.getValue());
            if (write.operation.equals("set")) {
                // Search for all reads to which there is a path
                for (OperationInterval read : nodes) {
                    // This is a read(get) operation
                    // If there is a path from write to read
//                    System.out.printf("2 %s, %s %n", read.operation, read.getValue());
                    if (read.operation.equals("get") && !write.getValue().equals(read.getValue()) && existsPath(write, read, map, new HashSet<>())) {
                        // Add read's dictating write
                        map.get(write).add(successors.get(read));
                    }
                }
            }
        }

        System.out.printf("-------All edges-------%n");
        print(map);

        return map;
    }

    private static boolean existsPath(OperationInterval write, OperationInterval read,
                                      Map<OperationInterval, HashSet<OperationInterval>> map,
                                      Set<OperationInterval> visited) {
        if (visited.contains(write)) {
            return false;
        }
        visited.add(write);
        if (write == read) {
            return true;
        }
        for (OperationInterval o : map.get(write)) {
            if (existsPath(o, read, map, visited)) {
                return true;
            }
        }
        return false;
    }

    private static void print(Map<OperationInterval, HashSet<OperationInterval>> map) {
        for (OperationInterval o : map.keySet()) {
            System.out.printf("[%s - %s] -> ", o.operation, o.getValue());
            for (OperationInterval oo : map.get(o)) {
                System.out.printf("[%s - %s] ", oo.operation, oo.getValue());
            }
            System.out.printf("%n");
        }
    }
}
