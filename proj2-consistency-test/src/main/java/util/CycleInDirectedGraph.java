package util;

/**
 * Created by Pan on 12/13/16.
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * http://www.geeksforgeeks.org/detect-cycle-in-a-graph/
 */
public class CycleInDirectedGraph {

    public static boolean hasCycle(Map<OperationInterval, HashSet<OperationInterval>> map) {
        Set<OperationInterval> whiteSet = new HashSet<>();
        Set<OperationInterval> graySet = new HashSet<>();
        Set<OperationInterval> blackSet = new HashSet<>();

        for (OperationInterval vertex : map.keySet()) {
            whiteSet.add(vertex);
        }

        while (whiteSet.size() > 0) {
            OperationInterval current = whiteSet.iterator().next();
            if(dfs(current, whiteSet, graySet, blackSet, map)) {
                return true;
            }
        }
        return false;
    }

    private static boolean dfs(OperationInterval current, Set<OperationInterval> whiteSet,
                        Set<OperationInterval> graySet, Set<OperationInterval> blackSet,
                        Map<OperationInterval, HashSet<OperationInterval>> map) {
        //move current to gray set from white set and then explore it.
        moveVertex(current, whiteSet, graySet);
        for(OperationInterval neighbor : map.get(current)) {
            //if in black set means already explored so continue.
            if (blackSet.contains(neighbor)) {
                continue;
            }
            //if in gray set then cycle found.
            if (graySet.contains(neighbor)) {
                return true;
            }
            if(dfs(neighbor, whiteSet, graySet, blackSet, map)) {
                return true;
            }
        }
        //move vertex from gray set to black set when done exploring.
        moveVertex(current, graySet, blackSet);
        return false;
    }

    private static void moveVertex(OperationInterval vertex, Set<OperationInterval> sourceSet,
                            Set<OperationInterval> destinationSet) {
        sourceSet.remove(vertex);
        destinationSet.add(vertex);
    }
}