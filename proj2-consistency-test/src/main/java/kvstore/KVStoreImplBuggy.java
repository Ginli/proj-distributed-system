package kvstore;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Pan on 10/8/16.
 */
public class KVStoreImplBuggy implements KVStore.Iface {
    private static final Logger logger = LoggerFactory.getLogger(KVStoreImpl.class);
    private static final ConcurrentMap<String, String> dataMap = new ConcurrentHashMap<String, String>();
    private static String cacheValue = "-99";

    public Result kvset(String key, String value) throws TException {
        logger.info(String.format("kvset: key = %s, value = %s", key, value));
        dataMap.put(key, value);
        if (System.currentTimeMillis() % 9 == 0){
            dataMap.put(key, cacheValue);
        } else if (System.currentTimeMillis() % 9 == 1){
            cacheValue = value;
        }
        return new Result("", ErrorCode.findByValue(0), "Set Successfully");
//        if (dataMap.containsKey(key) && dataMap.get(key).equals(value)) {
//            return new Result("", ErrorCode.findByValue(0), "Set Successfully");
//        } else {
//            return new Result("", ErrorCode.findByValue(2), "Fail to Set");
//        }
    }

    public Result kvget(String key) throws TException {
        if (dataMap.containsKey(key)) {
            String value = dataMap.get(key);
            logger.info(String.format("kvget: key = %s, value = %s", key, value));
            return new Result(dataMap.get(key), ErrorCode.findByValue(0), "Get Successfully");
        } else {
            logger.info(String.format("kvget: key = %s, value = NA", key));
            return new Result("", ErrorCode.findByValue(1), "Key Not Found");
        }
    }

    public Result kvdelete(String key) throws TException {
        logger.info(String.format("kvdelete: key = %s", key));
        if (dataMap.containsKey(key)) {
            dataMap.remove(key);
            return new Result("", ErrorCode.findByValue(0), "Delete Successfully");
        } else {
            return new Result("", ErrorCode.findByValue(1), "Key Not Found");
        }
    }
}
