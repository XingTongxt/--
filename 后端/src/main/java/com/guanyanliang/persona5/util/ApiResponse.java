package com.guanyanliang.persona5.util;

public class ApiResponse<T> {

    private int code;       // 状态码，比如 200, 400, 401
    private String message; // 提示信息
    private T data;         // 返回的数据，可以是对象、Map 或 null

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ===== Getter & Setter =====
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // ===== 静态快捷方法 =====
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "成功", data);
    }

    public static <T> ApiResponse<T> fail(String message, int code) {
        return new ApiResponse<>(code, message, null);
    }
}