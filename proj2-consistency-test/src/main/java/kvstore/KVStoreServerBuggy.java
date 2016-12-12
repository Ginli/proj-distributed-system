package kvstore;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pan on 10/8/16.
 */
public class KVStoreServerBuggy {
    private static final int SERVER_PORT = 9091;
    private static final Logger logger = LoggerFactory.getLogger(KVStoreServer.class);

    public static void main(String[] args) {
        TProcessor tprocessor = new KVStore.Processor<KVStore.Iface>(new KVStoreImplBuggy());
        try {
            TServerSocket serverTransport = new TServerSocket(SERVER_PORT);
            TServer.Args tArgs = new TServer.Args(serverTransport);
            tArgs.processor(tprocessor);
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            TServer server = new TSimpleServer(tArgs);
            logger.info("KVStore Java Server Buggy is running");
            server.serve();

        } catch (TTransportException e) {
            logger.error(e.toString());
        }
    }
}