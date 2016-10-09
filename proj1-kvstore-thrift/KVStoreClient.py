import sys
sys.path.append('gen-py')

import argparse

from KVStore import Client
from ttypes import ErrorCode, Result

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('-server', nargs='+')
    parser.add_argument('-set', nargs='+')
    parser.add_argument('-get', nargs='+')
    parser.add_argument('-del', nargs='+')
    args = parser.parse_args()

    # Make socket
    if 'server' in args:
        host_port = getattr(args, 'server')[0].split(':')
        if len(host_port) != 2:
            sys.stderr.write('Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val w/ outputFile>')
            return
        else:
            transport = TSocket.TSocket(host_port[0], host_port[1])
    else:
        transport = TSocket.TSocket('127.0.0.1', 9090)

    # Buffering is critical. Raw sockets are very slow
    transport = TTransport.TBufferedTransport(transport)

    # Wrap in a protocol
    protocol = TBinaryProtocol.TBinaryProtocol(transport)

    # Create a client to use the protocol encoder
    client = Client(protocol)

    # Connect!
    transport.open()

    # Test!
    result = None
    if getattr(args, 'set') != None:
        key_value = getattr(args, 'set')
        if len(key_value) != 2:
            sys.stderr.write('Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val w/ outputFile>')
            return
        else:
            result = set_kv(client, key_value[0], key_value[1])  
    if getattr(args, 'get') != None:
        key_file = getattr(args, 'get')
        if len(key_file) != 2:
            sys.stderr.write('Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val w/ outputFile>')
            return
        else:
            result = get_k(client, key_file[0])
            if result.error == 0:
                print result.value
                f = open(key_file[1],'w')
                f.write(result.value)
                f.close
    if getattr(args, 'del') != None:
        key = getattr(args, 'del')
        if len(key) != 1:
            sys.stderr.write('Usage: KVStoreClient -server <host:port> -CMD_NAME <key w/ val w/ outputFile>')
            return
        else:
            result = del_k(client, key[0])                                

    result_printer(result)
    

    # Close!
    transport.close()

def set_kv(client, key, val):
    return client.kvset(key, val)

def get_k(client, key):
    return client.kvget(key)

def del_k(client, key):
    return client.kvdelete(key)        

def result_printer(result):
    error_code = result.error
    if error_code == 0:
        print '0'
    else:
        print error_code
        print result.errortext



if __name__ == '__main__':
    main()