package kvstore;

import org.apache.commons.cli.CommandLine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pan on 12/9/16.
 */
public class KVStoreClientRunner implements Runnable {
    private int clientIndex;
    private String[] cmdServerPort;
    private String[][] argsPool;
    private Map<Integer, String> valMap;
    private int[] valMapRange;

    /*
        argsPoolSize << valMapRangeArr[1]-[0]
     */
    public KVStoreClientRunner(int clientI, String[] cmdServerPortStr, int argsPoolSize, int[] valMapRangeArr) {
        clientIndex = clientI;
        cmdServerPort = cmdServerPortStr;
        valMapRange = valMapRangeArr;
        initValMap();
        initArgsPool(argsPoolSize);
    }

    public void run() {
        KVStoreClient kvStoreClient = new KVStoreClient(clientIndex);
        CommandLine cmd = kvStoreClient.parseCmd(cmdServerPort);
        kvStoreClient.openTransport(cmd);
        initAllKeys(kvStoreClient);
        for (int i = 1; i < argsPool.length; i++) {
            kvStoreClient.cmdApply(kvStoreClient.parseCmd(argsPool[i]), i);
        }
        kvStoreClient.closeTransport();
    }

    private void initValMap() {
        valMap = new HashMap<Integer, String>();
        for (int i = valMapRange[0]; i < valMapRange[1]; i++) {
            valMap.put(i, String.valueOf(i));
        }
    }

    private void initArgsPool(int poolSize) {
        argsPool = new String[poolSize][];
        int valIndex = valMapRange[0];
        for(int i = 0; i < poolSize; i++) {
            argsPool[i] = i % 2 == 0 ? new String[]{"-get", "key1"} : new String[]{"-set", "key1", valMap.get(++valIndex)};     // why ++ first? first val is used for initialization
        }
    }

    private void initAllKeys(KVStoreClient kvStoreClient) {
        kvStoreClient.cmdApply(kvStoreClient.parseCmd(new String[]{"-set", "key1", valMap.get(valMapRange[0])}), -1);
    }
}

