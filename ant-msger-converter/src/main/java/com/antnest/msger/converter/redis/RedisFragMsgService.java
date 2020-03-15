package com.antnest.msger.converter.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * 缓存消息
 */
@Service
public class RedisFragMsgService {

    @Value("${redis.key.msg.frag}")
    private String redisKey;
    
    @Autowired
    private RedisUtils redisUtils;

    private static final long TIME_OUT_SECONDES = 3600 * 24 * 3; // 3天

//    @Autowired
//    private RedisTemplate redisTemplate;

    /**
     * 获取存在的分片号集合
     * @param clientSign
     * @param serialNumber
     * @return
     */
    public Set readMsgRedisFragNums(String clientSign, int serialNumber) {
        String key = combineRedisKey(clientSign, serialNumber);
        return redisUtils.hKeys(key);
    }

    public void saveMsgFrag(String clientSign, int serialNumber, int subPackageNumber, byte[] array) {
        String key = combineRedisKey(clientSign, serialNumber);
        try {
            redisUtils.hPut(key, subPackageNumber+"", array);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Map<Integer, byte[]>
     * @param clientSign
     * @param serialNumber
     * @return
     */
    public Map<Object, Object> readMsgs(String clientSign, int serialNumber) {
        String key = combineRedisKey(clientSign, serialNumber);
        return redisUtils.hGetAll(key);
    }

    private String combineRedisKey(String clientSign, int serialNumber) {
        return redisKey.concat(":").concat(clientSign).concat(":").concat(serialNumber + "");
    }
}
