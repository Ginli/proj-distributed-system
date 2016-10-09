package gen_java.kvstore;

import org.apache.commons.cli.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Pan on 10/8/16.
 */
public class KVStoreClient {
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 9090;
    public static final int TIMEOUT = 30000;

    public static void main(String[] args) throws TException, ParseException, FileNotFoundException {
        TTransport transport = new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT);

        Options options = new Options();
        options.addOption("server", true, "host:port");
        Option optionSet = new Option("set", true, "set val by key");
        optionSet.setArgs(2);
        options.addOption(optionSet);
        Option optionGet = new Option("get", true, "get val by key");
        optionGet.setArgs(2);
        options.addOption(optionGet);
        options.addOption("del", true, "delete val by key");

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = commandLineParser.parse(options, args);
        } catch (MissingArgumentException e) {
            System.err.println("Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val w/ outputFile>");
            return ;
        }

        if (cmd.hasOption("server")) {
            String serverPort = cmd.getOptionValue("server");
            List<String> serverPortList = Arrays.asList(serverPort.split(":"));
            if (serverPortList.size() != 2) {
                return ;
            }
            transport = new TSocket(serverPortList.get(0), Integer.parseInt(serverPortList.get(1)), TIMEOUT);
        }

        TProtocol protocol = new TBinaryProtocol(transport);
        KVStore.Client client = new KVStore.Client(protocol);
        transport.open();

        Result result = null;
        if (cmd.hasOption("set")) {
            String[] keyValue = cmd.getOptionValues("set");
            if (keyValue.length != 2) {
                System.err.println("Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val w/ outputFile>");
                return ;
            }
            result = set(client, keyValue[0], keyValue[1]);
        } else if (cmd.hasOption("get")) {
            String[] keyFile = cmd.getOptionValues("get");
            if (keyFile.length != 2) {
                System.err.println("Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val w/ outputFile>");
                return ;
            }
            result = get(client, keyFile[0]);
            if (result.getError().getValue() == 0) {
                System.out.println(result.getValue());
                PrintWriter printWriter = new PrintWriter(keyFile[1]);
                printWriter.println(result.getValue());
                printWriter.close();
            }
        } else if (cmd.hasOption("del")) {
            String key = cmd.getOptionValue("del");
            if (key == "") {
                System.err.println("Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val w/ outputFile>");
                return ;
            }
            result = delete(client, key);
        }

        resultPrinter(result);

        transport.close();
    }

    private static void resultPrinter(Result result) {
        int errorCode = result.getError().getValue();
        if (errorCode == 0) {
            System.out.println("0");
        } else {
            System.err.println(errorCode);
            System.err.println(result.getErrortext());
        }
    }

    private static Result set(KVStore.Client client, String key, String value) throws TException {
        return client.kvset(key, value);
    }

    private static Result get(KVStore.Client client, String key) throws TException {
        return client.kvget(key);
    }

    private static Result delete(KVStore.Client client, String key) throws TException {
        return client.kvdelete(key);
    }
}
