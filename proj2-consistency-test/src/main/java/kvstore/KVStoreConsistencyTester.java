package kvstore;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.*;
import util.*;
import util.exceptions.ConnectionErrorException;

/**
 * Created by Pan on 12/9/16.
 */
public class KVStoreConsistencyTester {
    public static void main(String[] args) {
//        String[] cmdServerPort = new String[] {"-server", "127.0.0.1:9090"};    // normal
//        String[] cmdServerPort = new String[] {"-server", "127.0.0.1:9091"};  // buggy

        String[] cmdServerPort;
        int errorcode = 2, threadNum = 8, argsPoolSize = 100;
        Options options = initOptions();
        CommandLine cmd = parseCmd(args, options);

        if (cmd.hasOption("server")) {
            cmdServerPort = new String[]{"-server", cmd.getOptionValue("server")};
            if (cmd.hasOption("threadNum")) {
                threadNum = Integer.parseInt(cmd.getOptionValue("threadNum"));
            }
            if (cmd.hasOption("argsPoolSize")) {
                argsPoolSize = Integer.parseInt(cmd.getOptionValue("argsPoolSize"));
            }
            errorcode = runTest(threadNum, cmdServerPort, argsPoolSize);
        } else {
            System.err.println("Usage: ./KVStoreConsistencyTester -server <host:port> <-threadNum 16 -argsPoolSize 100>");
            errorcode = 2;
        }


        System.out.println("Error code:\t" + errorcode);
        System.exit(errorcode);
    }

    private static int runTest(int threadNum, String[] cmdServerPort, int argsPoolSize) {
        renameLogDir();
        int errorCode = 0;
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
                System.err.printf("Failed to stop threads.%s%n", e.getMessage());
            }
        }

        try {
            List<OperationInterval> intervals = new LogParser().startParsingLogs("./src/main/log", threadNum);
            errorCode = AtomicChecker.check(GraphGenerator.generateGraph(intervals));
        } catch (ConnectionErrorException e) {
            errorCode = 2;
        } catch (IOException e) {
            System.err.printf("Cannot open log files.%s%n", e.getMessage());
            errorCode = 2;
        }

        renameLogDir();
        return errorCode;
    }

    private static void renameLogDir() {
        File logDir = new File("./src/main/log");
        if (!logDir.isDirectory()) {
            return;
        }
        File newLogDir = new File(logDir.getParent() + "/log_" + String.valueOf(System.currentTimeMillis()));
        logDir.renameTo(newLogDir);
    }

    protected static Options initOptions() {
        Options options = new Options();
        options.addOption("server", true, "host:port");
        options.addOption("threadNum", true, "thread num");
        options.addOption("argsPoolSize", true, "request pool size");
        return options;
    }

    protected static CommandLine parseCmd(String[] args, Options options) {
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;
        try {
            // checking options's values validation is handled here as MissingArgumentException
            cmd = commandLineParser.parse(options, args);
            return cmd;
        } catch (ParseException e) {
            System.err.println("Usage: ./KVStoreConsistencyTester -server <host:port> <-threadNum 20 -argsPoolSize 30>");
        }
        return null;
    }
}
