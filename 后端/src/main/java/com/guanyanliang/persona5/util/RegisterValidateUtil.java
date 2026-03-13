package com.guanyanliang.persona5.util;

import java.util.regex.Pattern;

/**
 * 注册参数校验工具类
 */
public class RegisterValidateUtil {
    // 密码规则：8-20位，包含大小写字母+数字（无特殊字符，若需特殊字符可调整正则）
    private static final String PWD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,20}$";
    // 邮箱正则（通用版）
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";

    /**
     * 校验密码格式
     */
    public static boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }
        return Pattern.matches(PWD_REGEX, password);
    }

    /**
     * 校验邮箱格式
     */
    public static boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }
}