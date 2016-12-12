package kvstore;

import java.io.File;

/**
 * Created by Pan on 12/9/16.
 */
public class KVStoreConsistencyTester {
    public static void main(String[] args) {
        String[] cmdServerPort = new String[] {"-server", "127.0.0.1:9090"};    // normal
//        String[] cmdServerPort = new String[] {"-server", "127.0.0.1:9091"};  // buggy
        runTest(5, cmdServerPort, 50);
    }

    private static void renameLogDir() {
        File logDir = new File("./src/main/log");
        if (!logDir.isDirectory()) {
            System.err.println("There is no log directory.");
            return;
        }
        File newLogDir = new File(logDir.getParent() + "/log_" + String.valueOf(System.currentTimeMillis()));
        logDir.renameTo(newLogDir);
    }

    private static void runTest(int threadNum, String[] cmdServerPort, int argsPoolSize) {
        int valMapRangeStart = 0;
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            KVStoreClientRunner kvStoreClientRunner = new KVStoreClientRunner(i, cmdServerPort, argsPoolSize, new int[] {valMapRangeStart, valMapRangeStart + argsPoolSize});
            threads[i] = new Thread(kvStoreClientRunner);
            threads[i].start();
            valMapRangeStart += argsPoolSize;
        }

        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
            Add code here
         */


        renameLogDir();
    }
}
