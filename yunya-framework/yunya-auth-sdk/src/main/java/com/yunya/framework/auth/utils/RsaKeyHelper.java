package com.yunya.framework.auth.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Rsa密码加密工具类
 *
 * @author ace
 * @date 2017/9/10
 */
public class RsaKeyHelper {

  /**
   * 获取公钥
   *
   * @param filename
   * @return
   * @throws Exception
   */
  public PublicKey getPublicKey(String filename) throws Exception {
    InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filename);
    assert resourceAsStream != null;
    DataInputStream dis = new DataInputStream(resourceAsStream);
    byte[] keyBytes = new byte[resourceAsStream.available()];
    dis.readFully(keyBytes);
    dis.close();
    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePublic(spec);
  }

  /**
   * 获取密钥
   *
   * @param filename
   * @return
   * @throws Exception
   */
  public PrivateKey getPrivateKey(String filename) throws Exception {
    InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filename);
    assert resourceAsStream != null;
    DataInputStream dis = new DataInputStream(resourceAsStream);
    byte[] keyBytes = new byte[resourceAsStream.available()];
    dis.readFully(keyBytes);
    dis.close();
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePrivate(spec);
  }

  /**
   * 获取公钥
   *
   * @param publicKey
   * @return
   * @throws Exception
   */
  public PublicKey getPublicKey(byte[] publicKey) throws Exception {
    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePublic(spec);
  }

  /**
   * 获取密钥
   *
   * @param privateKey
   * @return
   * @throws Exception
   */
  public PrivateKey getPrivateKey(byte[] privateKey) throws Exception {
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePrivate(spec);
  }

  /**
   * 生存rsa公钥和密钥
   *
   * @param publicKeyFilename
   * @param privateKeyFilename
   * @param password
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  public void generateKey(String publicKeyFilename, String privateKeyFilename, String password)
      throws IOException, NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    SecureRandom secureRandom = new SecureRandom(password.getBytes());
    keyPairGenerator.initialize(1024, secureRandom);
    KeyPair keyPair = keyPairGenerator.genKeyPair();
    byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
    FileOutputStream fos = new FileOutputStream(publicKeyFilename);
    fos.write(publicKeyBytes);
    fos.close();
    byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
    fos = new FileOutputStream(privateKeyFilename);
    fos.write(privateKeyBytes);
    fos.close();
  }

  /**
   * 生存rsa公钥
   *
   * @param password
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  public static byte[] generatePublicKey(String password)
      throws IOException, NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    SecureRandom secureRandom = new SecureRandom(password.getBytes());
    keyPairGenerator.initialize(1024, secureRandom);
    KeyPair keyPair = keyPairGenerator.genKeyPair();
    return keyPair.getPublic().getEncoded();
  }

  /**
   * 生存rsa公钥
   *
   * @param password
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  public static byte[] generatePrivateKey(String password)
      throws IOException, NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    SecureRandom secureRandom = new SecureRandom(password.getBytes());
    keyPairGenerator.initialize(1024, secureRandom);
    KeyPair keyPair = keyPairGenerator.genKeyPair();
    return keyPair.getPrivate().getEncoded();
  }

  /**
   * 初始化公钥、密钥
   *
   * @param password 密码
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  public static Map<String, byte[]> generateKey(String password)
      throws IOException, NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    SecureRandom secureRandom = new SecureRandom(password.getBytes());
    keyPairGenerator.initialize(1024, secureRandom);
    KeyPair keyPair = keyPairGenerator.genKeyPair();
    byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
    byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
    Map<String, byte[]> map = new HashMap<>(16);
    map.put("pub", publicKeyBytes);
    map.put("pri", privateKeyBytes);
    return map;
  }

  /**
   * 将字节数组装成16进制
   *
   * @param b
   * @return
   */
  public static String toHexString(byte[] b) {
    return new String(Base64.encodeBase64(b));
  }

  /**
   * 将字符串转字节数组
   *
   * @param s
   * @return
   * @throws IOException
   */
  public static byte[] toBytes(String s) throws IOException {
    return Base64.decodeBase64(s);
  }
}
