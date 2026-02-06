package com.cjy.common;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Integer code;
    private String message;
    private Object data;


    public static Result success(Object data) {
        return new Result(0, "success", data);
    }

    public static Result success() {
        return new Result(0, "success", null);
    }
    public static Result error(String message) {
        return new Result(1, message, null);
    }
    public static Result success(Object data, String message) {
        return new Result(0, message, data);
    }
}
