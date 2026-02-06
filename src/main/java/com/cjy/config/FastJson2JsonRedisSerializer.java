package com.cjy.config;

import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {

    private final Class<T> type;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public FastJson2JsonRedisSerializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            return JSON.toJSONString(t).getBytes(DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new SerializationException("Could not serialize object", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            String json = new String(bytes, DEFAULT_CHARSET);
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            throw new SerializationException("Could not deserialize object", e);
        }
    }
}
