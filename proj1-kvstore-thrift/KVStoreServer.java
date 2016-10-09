package gen_java.kvstore;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;

/**
 * Created by Pan on 10/8/16.
 */
public class KVStoreServer {
    public static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws TException{
        TProcessor tprocessor = new KVStore.Processor<KVStore.Iface>(new KVStoreImpl());
        TServerSocket serverTransport = new TServerSocket(SERVER_PORT);
        TServer.Args tArgs = new TServer.Args(serverTransport);
        tArgs.processor(tprocessor);
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
        TServer server = new TSimpleServer(tArgs);
        System.out.println(System.currentTimeMillis() + "\tKVStore Java Server is running....");
        server.serve();
    }
}
