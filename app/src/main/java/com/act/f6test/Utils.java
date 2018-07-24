package com.act.f6test;

/**
 * Created by chy on 15-5-12.
 */
public final class Utils {
    public static String bytesToHexString(byte[] bytes) {
        final char[] rgHexChars = "0123456789ABCDEF".toCharArray();
        char[] chars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            chars[j * 3] = rgHexChars[v >>> 4];
            chars[j * 3 + 1] = rgHexChars[v & 0x0F];
            chars[j * 3 + 2] = ' ';
        }
        return new String(chars);
    }


    public static byte[] hexStringToBytes(String strHex) {
        if (strHex == null || strHex.equals("")) {
            return null;
        }
        strHex = strHex.toUpperCase();
        strHex = strHex.replaceAll(" ", "");
        int length = strHex.length() / 2;
        char[] chars = strHex.toCharArray();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) ((hexValue(chars[i * 2]) << 4) | hexValue(chars[i * 2 + 1]));
        }
        return bytes;
    }

    private static int hexValue(char c) {
        if (Character.isDigit(c))
            return (int) (c - 0x30);
        else
            return (int) (c - 'A' + 10);
    }
}
