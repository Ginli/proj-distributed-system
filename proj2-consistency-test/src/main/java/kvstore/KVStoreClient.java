package kvstore;

import org.apache.commons.cli.*;
import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;


/**
 * Created by Pan on 10/8/16.
 */
public class KVStoreClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    private static final int TIMEOUT = 3000;
    private TTransport transport;                       // NOT static
    private KVStore.Client client;                      // NOT static
    private static Options options;
    private Logger logger;                              // NOT static

//    public static void main(String[] args) {
//        KVStoreClient kvStoreClient = new KVStoreClient(1);
//        CommandLine cmd = kvStoreClient.parseCmd(args);
//        kvStoreClient.openTransport(cmd);
//        kvStoreClient.cmdApply(cmd, 0);
//        kvStoreClient.closeTransport();
//    }

    public KVStoreClient(int clientIndex) {
        logger = LoggerFactory.getLogger("client_" + String.valueOf(clientIndex));
        setLoggerProps(clientIndex);
        initOptions();
    }

    private void setLoggerProps(int clientIndex) {
        Properties props = new Properties();
        props.setProperty("log4j.logger.client_" + String.valueOf(clientIndex),"DEBUG, file");
        props.setProperty("log4j.appender.file", "org.apache.log4j.RollingFileAppender");
        props.setProperty("log4j.appender.file.File", "./src/main/log/client_" + String.valueOf(clientIndex) + ".log");
        props.setProperty("log4j.appender.file.MaxFileSize", "10MB");
        props.setProperty("log4j.appender.file.MaxBackupIndex", "10");
        props.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
        props.setProperty("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss:SSS} %-5p %c{1}:%L - %m%n");
        PropertyConfigurator.configure(props);
    }

    protected void initOptions() {
        options = new Options();
        options.addOption("server", true, "host:port");
        Option optionSet = new Option("set", true, "set val by key");
        optionSet.setArgs(2);
        options.addOption(optionSet);
        options.addOption("get", true, "get val by key");
        options.addOption("del", true, "delete val by key");
    }

    protected CommandLine parseCmd(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmd;
        try {
            // checking options's values validation is handled here as MissingArgumentException
            cmd = commandLineParser.parse(options, args);
            return cmd;
        } catch (ParseException e) {
            System.err.println("Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val>");
            errorHandler(e);
        }
        return null;
    }

    protected void openTransport(CommandLine cmd) {
        transport = new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT);

        if (cmd.hasOption("server")) {
            String[] serverAndPort = cmd.getOptionValue("server").split(":");
            transport = new TSocket(serverAndPort[0], Integer.parseInt(serverAndPort[1]), TIMEOUT);
        }

        TProtocol protocol = new TBinaryProtocol(transport);
        client = new KVStore.Client(protocol);
        try {
            transport.open();
        } catch (TTransportException e) {
            errorHandler(e);
        }
    }

    protected void closeTransport() {
        transport.close();
    }

    // reused for consistency testing
    protected Result cmdApply(CommandLine cmd, int cmdIndex) {
        Result result = null;
        try {
            if (cmd.hasOption("set")) {
                String[] keyAndValue = cmd.getOptionValues("set");
                logger.info(String.format("cmdIndex=%d, cmd=%s, timeType=start, key=%s, val=%s", cmdIndex, "set", keyAndValue[0], keyAndValue[1]));
                result = client.kvset(keyAndValue[0], keyAndValue[1]);
                if (resultChecker(result)) logger.info(String.format("cmdIndex=%d, cmd=%s, timeType=finish, key=%s, val=%s", cmdIndex, "set", keyAndValue[0], keyAndValue[1]));
            } else if (cmd.hasOption("get")) {
                String[] key = cmd.getOptionValues("get");
                logger.info(String.format("cmdIndex=%d, cmd=%s, timeType=start, key=%s", cmdIndex, "get", key[0]));
                result = client.kvget(key[0]);
                if (resultChecker(result)) logger.info(String.format("cmdIndex=%d, cmd=%s, timeType=finish, key=%s, val=%s", cmdIndex, "get", key[0], result.getValue()));
            } else if (cmd.hasOption("del")) {
                String[] key = cmd.getOptionValues("del");
                logger.info(String.format("cmdIndex=%d, cmd=%s, timeType=start, key=%s", cmdIndex, "del", key[0]));
                result = client.kvdelete(key[0]);
                if (resultChecker(result)) logger.info(String.format("cmdIndex=%d, cmd=%s, timeType=finish, key=%s", cmdIndex, "del", key[0]));
            }
        } catch (TException e) {
            errorHandler(e);
        }
        return result;
    }

    private boolean resultChecker(Result result) {
        if (result == null) {
            logger.error(String.format("error=%s", "Result is null"));
            errorHandler(new Exception("Result is null"));
        }
        int errorCode = result.getError().getValue();
        if (errorCode == 0) {
            return true;
        } else {
            logger.error(String.format("error=%s", result.getErrortext()));
            System.err.println(result.getErrortext());
//            System.exit(errorCode);
        }
        return false;
    }

    private void errorHandler(Exception e) {
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        e.printStackTrace(pw);
//        logger.error(sw.toString());
        logger.error(e.toString());
//        System.exit(2);
    }
}
