package gen_java.kvstore;

import org.apache.thrift.TException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pan on 10/8/16.
 */
public class KVStoreImpl implements KVStore.Iface {
    private static Map<String, String> dataMap = new HashMap<String, String>() {{
        put("key1", "value1");
        put("key2", "value2");
        put("key3", "value3");
        put("key4", "value4");
        put("key5", "value5");
    }};

    public Result kvset(String key, String value) throws TException {
        System.out.println(System.currentTimeMillis() + "\tcall kvset(" + key + ", " + value + ")");
        dataMap.put(key, value);
        if (dataMap.containsKey(key) && dataMap.get(key) == value) {
            return new Result("", ErrorCode.findByValue(0), "Set Successfully");
        } else {
            return new Result("", ErrorCode.findByValue(2), "Fail to Set");
        }
    }

    public Result kvget(String key) throws TException {
        System.out.println(System.currentTimeMillis() + "\tcall kvget(" + key + ")");
        if (dataMap.containsKey(key)) {
            return new Result(dataMap.get(key), ErrorCode.findByValue(0), "Get Successfully");
        } else {
            return new Result("", ErrorCode.findByValue(1), "Key Not Found");
        }
    }

    public Result kvdelete(String key) throws TException {
        System.out.println(System.currentTimeMillis() + "\tcall kvdelete(" + key + ")");
        if (dataMap.containsKey(key)) {
            dataMap.remove(key);
            return new Result("", ErrorCode.findByValue(0), "Delete Successfully");
        } else {
            return new Result("", ErrorCode.findByValue(1), "Key Not Found");
        }
    }
}
