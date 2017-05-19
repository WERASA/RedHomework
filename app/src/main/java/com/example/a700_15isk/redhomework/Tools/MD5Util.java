package com.example.a700_15isk.redhomework.Tools;

/**
 * Created by 700-15isk on 2017/3/11.
 */


    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;

    /**
     * Created by Pinger on 2016/7/23.
     */
    public class MD5Util {
        private MD5Util() {
        }

        /**
         * md5加密
         * @param password
         * @return
         */
        public static String encodeMd5(String password) {
            try {
                // 得到信息摘要器
                MessageDigest digest = MessageDigest.getInstance("md5");
                // 通过信息摘要器，将密码字符串转换成字节数组
                byte[] bys = digest.digest(password.getBytes());

                // 定义一个字符串缓冲区存储字符串
                StringBuffer buffer = new StringBuffer();

                // 遍历得到每一个字节
                for (byte b : bys) {
                    // 每一个字节都与上一个十六进制，得到十进制数
                    int number = b & 0xff;
                    // 将十进制数转换成十六进制数进行显示
                    String str = Integer.toHexString(number);
                    // 如果得到的十六进制数只有四位，则需要将其补全到八位
                    if (str.length() == 1) {
                        buffer.append("0");
                    }
                    // 将结果添加到缓冲区
                    buffer.append(str);
                }
                // 返回结果
                return buffer.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

