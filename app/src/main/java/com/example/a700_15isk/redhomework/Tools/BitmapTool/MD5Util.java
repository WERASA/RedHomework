package com.example.a700_15isk.redhomework.Tools.BitmapTool;

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

        public static String encodeMd5(String password) {
            try {

                MessageDigest digest = MessageDigest.getInstance("md5");

                byte[] bys = digest.digest(password.getBytes());


                StringBuffer buffer = new StringBuffer();

                for (byte b : bys) {

                    int number = b & 0xff;

                    String str = Integer.toHexString(number);

                    if (str.length() == 1) {
                        buffer.append("0");
                    }

                    buffer.append(str);
                }

                return buffer.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

