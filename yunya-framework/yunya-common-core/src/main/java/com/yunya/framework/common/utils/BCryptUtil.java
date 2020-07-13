package com.yunya.framework.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCrypt加密工具类
 *
 * @author chow
 */
public class BCryptUtil {
  /**
   * 加密
   *
   * @param password 密码
   * @return String
   */
  public static String encode(String password) {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String hashPass = passwordEncoder.encode(password);
    return hashPass;
  }

  /**
   * 解密
   *
   * @param password 密码
   * @param hashPass 哈希值
   * @return
   */
  public static boolean matches(String password, String hashPass) {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    boolean f = passwordEncoder.matches(password, hashPass);
    return f;
  }
}
