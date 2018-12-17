/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  2012-4-5 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.endec;

import java.security.MessageDigest;

/**
 * 标准MD5加密方法，使用java类库的security包的MessageDigest类处理 
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date 2012-4-5 下午01:41:42
 * 
 * @see
 */
public class MD5 {
	
	 /** 
     * 获得MD5加密密码的方法 
     */  
    public static String getMD5ofStr(String origString) {  
        String origMD5 = null;  
        try {  
            MessageDigest md5 = MessageDigest.getInstance("MD5");  
            byte[] result = md5.digest(origString.getBytes());  
            origMD5 = byteArray2HexStr(result);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return origMD5;  
    }  
    /** 
     * 处理字节数组得到MD5密码的方法 
     */  
    private static String byteArray2HexStr(byte[] bs) {  
        StringBuffer sb = new StringBuffer();  
        for (byte b : bs) {  
            sb.append(byte2HexStr(b));  
        }  
        return sb.toString();  
    }  
    /** 
     * 字节标准移位转十六进制方法 
     */  
    private static String byte2HexStr(byte b) {  
        String hexStr = null;  
        int n = b;  
        if (n < 0) {  
            //若需要自定义加密,请修改这个移位算法即可  
            n = b & 0x7F + 128;  
        }  
        hexStr = Integer.toHexString(n / 16) + Integer.toHexString(n % 16);  
        return hexStr.toUpperCase();  
    }  
    /** 
     * 提供一个MD5多次加密方法 
     */  
    public static String getMD5ofStr(String origString, int times) {  
        String md5 = getMD5ofStr(origString);  
        for (int i = 0; i < times - 1; i++) {  
            md5 = getMD5ofStr(md5);  
        }  
        return getMD5ofStr(md5);  
    }  
  
    /** 
     * 重载一个多次加密时的密码验证方法 
     */  
    public static boolean verifyPassword(String inputStr, String MD5Code, int times) {  
        return getMD5ofStr(inputStr, times).equals(MD5Code);  
    }
    
    /** 
     * 提供一个测试的主函数 
     */  
    public static void main(String[] args) {  
        System.out.println("wjb:" + getMD5ofStr("wjb", 3));  
        System.out.println("bxl:" + getMD5ofStr("bxl", 3));  
        System.out.println("hjt:" + getMD5ofStr("htj", 3));  
        System.out.println("swz:" + getMD5ofStr("swz", 3));  
        System.out.println("sjy:" + getMD5ofStr("sjy", 3)); 
        System.out.println("sjy:" + getMD5ofStr("", 3)); 
    }  
}
