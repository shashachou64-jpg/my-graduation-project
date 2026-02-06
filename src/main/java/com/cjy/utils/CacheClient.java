package com.cjy.utils;

import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.cjy.common.RedisData;
import com.cjy.common.RedisConstants;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

@Slf4j
@Component
public class CacheClient {
    private final StringRedisTemplate stringRedisTemplate;
    /** 随机数实例（用于防止缓存雪崩） */
    private final Random random = new Random();

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 设置缓存，并设置过期时间
     * 防止缓存穿透
     * 
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), time, unit);
    }

    /**
     * 防止缓存穿透
     * 设置缓存，并设置过期时间
     * 
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void setWithLogicExpire(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(redisData));
    }

    // ===============================
    // 🔐 防止缓存雪崩方案
    // ===============================

    /**
     * 设置缓存（带随机过期时间，防止雪崩）
     * 原理：在基础过期时间上增加随机偏移量，使缓存不会同时失效
     * 
     * @param key      缓存key
     * @param value    缓存value
     * @param time     基础过期时间
     * @param unit     时间单位
     */
    public void setWithRandomExpire(String key, Object value, Long time, TimeUnit unit) {
        // 计算随机偏移量：基础时间的 5%~10%
        long randomOffset = (long) (time * 0.05) + random.nextInt((int) (time * 0.05));
        // 最终过期时间 = 基础时间 + 随机偏移量
        long finalTime = time + randomOffset;
        
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), finalTime, unit);
    }

    /**
     * 设置缓存（带随机过期时间和逻辑过期时间，防止雪崩）
     * 
     * @param key      缓存key
     * @param value    缓存value
     * @param time     基础过期时间
     * @param unit     时间单位
     */
    public void setWithLogicExpireAndRandom(String key, Object value, Long time, TimeUnit unit) {
        // 计算随机偏移量：基础时间的 5%~10%
        long randomOffset = (long) (time * 0.05) + random.nextInt((int) (time * 0.05));
        // 最终过期时间 = 基础时间 + 随机偏移量
        long finalTime = time + randomOffset;
        
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(finalTime)));
        // 设置 Redis 过期时间，防止 TTL 为 -1
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(redisData), finalTime, unit);
    }

    /**
     * 防止缓存穿透
     * 查询缓存，并设置过期时间
     * 
     * @param keyPrefix
     * @param id
     * @param type
     * @param dbFallback
     * @param time
     * @param unit
     * @return
     */
    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time,
            TimeUnit unit) {
        String key = keyPrefix + id;
        // 从redis中获取数据
        String json = stringRedisTemplate.opsForValue().get(key);
        // 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            // 存在，直接返回
            log.info("====================从redis返回====================");
            return JSON.parseObject(json, type);
        }
        // 判断是否是空值
        if (json != null) {
            log.warn("====================redis中的数据为空====================");
            return null;
        }
        // 不存在，从数据库中查询
        log.info("====================从redis返回====================");
        R r = dbFallback.apply(id);

        if (r == null) {
            return null;
        }
        // 将数据写入redis
        log.info("====================开始写入redis====================");
        this.set(key, r, time, unit);

        return r;
    }

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 互斥锁
     * 
     * 
     */
    public <R, ID> R queryWithLogicExpire(String keyPrefix, ID id,
            TypeReference<R> typeRef,
            Function<ID, R> dbFallback,
            Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        
        String json = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(json)) {
            // 缓存为空，调用数据库查询
            R r = dbFallback.apply(id);
            if (r == null) {
                return null;
            }
            // 写入缓存
            this.setWithLogicExpire(key, r, time, unit);
            return r;
        }

        // 判断缓存格式
        String trimmedJson = json.trim();
        R result = null;

        if (trimmedJson.startsWith("[")) {
            // 数组格式，直接解析
            try {
                result = parseByTypeReference(trimmedJson, typeRef);
                if (result != null) {
                    return result;
                }
            } catch (Exception e) {
                log.warn("缓存数组格式解析失败: {}", e.getMessage());
            }
        } else if (trimmedJson.startsWith("{")) {
            // 对象格式，尝试解析为 RedisData
            try {
        RedisData redisData = JSON.parseObject(json, RedisData.class);
                if (redisData != null && redisData.getData() != null) {
                    // 解析 RedisData 中的 data 字段
                    Object data = redisData.getData();
                    String dataJson = (data instanceof String) ? (String) data : JSON.toJSONString(data);
                    result = parseByTypeReference(dataJson, typeRef);

                    if (result != null) {
                        // 检查是否过期
        if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
                            return result;
        }
                        // 已过期，异步重建缓存
        String lockKey = RedisConstants.LOCK_PREFIX + key;
        boolean isLock = tryLock(lockKey);
        if (isLock) {
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    R r1 = dbFallback.apply(id);
                                    if (r1 != null) {
                                        this.setWithLogicExpire(key, r1, time, unit);
                    }
                } finally {
                    unLock(lockKey);
                }
            });
        }
                        return result;
                    }
                }
            } catch (Exception e) {
                log.warn("RedisData 格式解析失败: {}", e.getMessage());
            }
        }

        // 解析失败，清除缓存重新查询
        log.error("缓存数据解析失败，清除缓存重新查询");
        stringRedisTemplate.delete(key);
        R r = dbFallback.apply(id);
        if (r == null) {
            return null;
        }
        this.setWithLogicExpire(key, r, time, unit);
        return r;
    }

    /**
     * 根据 TypeReference 解析 JSON 字符串
     * 支持数组和对象格式
     */
    @SuppressWarnings("unchecked")
    private <R> R parseByTypeReference(String json, TypeReference<R> typeRef) {
        Type type = typeRef.getType();
        
        // 获取原始类型
        Class<?> rawType = null;
        if (type instanceof Class<?>) {
            rawType = (Class<?>) type;
        } else if (type instanceof java.lang.reflect.ParameterizedType) {
            rawType = (Class<?>) ((java.lang.reflect.ParameterizedType) type).getRawType();
        }

        if (rawType == null) {
            return null;
        }

        // 判断是否是 List 类型
        if (List.class.isAssignableFrom(rawType)) {
            // 获取 List 的泛型类型
            Type elementType = null;
            if (type instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.Type[] actualTypeArguments = 
                    ((java.lang.reflect.ParameterizedType) type).getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    elementType = actualTypeArguments[0];
                    if (elementType instanceof Class<?>) {
                        return (R) JSON.parseArray(json, (Class<?>) elementType);
                    }
                }
            }
            // 如果无法获取泛型类型，尝试直接解析为 JSONArray 后转换
            JSONArray array = JSON.parseArray(json);
            if (array != null) {
                return (R) array;
            }
        } else {
            // 普通对象类型
            return JSON.parseObject(json, typeRef);
        }
        
        return null;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(flag);
    }

    private void unLock(String key) {
        stringRedisTemplate.delete(key);
    }

}
