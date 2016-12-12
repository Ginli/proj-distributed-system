package main.java.algorithm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by raylee on 12/12/16.
 */
public class AtomicChecker {
    private static boolean flag = false;
    private static Set<OperationInterval> checked = new HashSet<>();

    public static int check(Map<OperationInterval, HashSet<OperationInterval>> map) {
        return checkCircle(map) ? 1 : 0;
    }

    private static boolean checkCircle(Map<OperationInterval, HashSet<OperationInterval>> map) {
        Set<OperationInterval> set = new HashSet<>();
        for (OperationInterval key: map.keySet()) {
            if (!checked.contains(key)) {
                helper(map, key, set);
            }
        }
        return flag;
    }
    private static void helper(Map<OperationInterval, HashSet<OperationInterval>> map,OperationInterval node,
                               Set<OperationInterval> set){
        if(!checked.contains(node)){
            if(set.contains(node)){
                flag = true;
                return;
            }
            set.add(node);
            if(flag == false && map.containsKey(node)){
                //System.out.print(node.name);
                Set<OperationInterval> neighbors = map.get(node);
                for (OperationInterval key: neighbors) {
                    helper(map, key,set);
                }
            }
            set.remove(node);
        }
        checked.add(node);
    }
}
