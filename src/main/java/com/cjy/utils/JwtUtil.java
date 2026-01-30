package com.cjy.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String KEY = "cjy";

    public static String genToken(Map<String, Object> claims) {
        return JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 10000))
                .sign(Algorithm.HMAC256(KEY));
    }

    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims").asMap();
    }

    public static String getUsername(String token) {
        Map<String, Object> claims = parseToken(token);
        return (String) claims.get("username");
    }

    public static Long getId(String token) {
        Map<String, Object> claims = parseToken(token);
        Object id = claims.get("id");
        if (id == null) {
            return null;
        }
        // 处理 Long 或 Integer 类型
        if (id instanceof Long) {
            return (Long) id;
        } else if (id instanceof Integer) {
            return ((Integer) id).longValue();
        }
        return Long.parseLong(id.toString());
    }
}
