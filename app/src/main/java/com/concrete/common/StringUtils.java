package com.concrete.common;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by Tangxl on 2017/12/10.
 */

public class StringUtils {
    private StringUtils() {
    }

    /**
     * Convert a byte array into its hexadecimal string representation.
     * @param b  Byte array.
     * @return   Hexadecimal string representation.
     */
    public static String convertByteArrayToHexString(byte[] b) {
        if (b != null) {
            StringBuilder s = new StringBuilder(2 * b.length);

            for (int i = 0; i < b.length; ++i) {
                final String t = Integer.toHexString(b[i]);
                final int l = t.length();
                if (l > 2) {
                    s.append(t.substring(l - 2));
                } else {
                    if (l == 1) {
                        s.append("0");
                    }
                    s.append(t);
                }
            }

            return s.toString();
        } else {
            return "";
        }
    }

    /**
     * Reverse a byte array and convert it into its hexadecimal string representation.
     * @param b  Byte array.
     * @return   Reverse hexadecimal string representation.
     */
    public static String convertByteArrayToReverseHexString(byte[] b) {
        if (b != null) {
            StringBuilder s = new StringBuilder(2 * b.length);

            for (int i = (b.length - 1); i >= 0; --i) {
                final String t = Integer.toHexString(b[i]);
                final int l = t.length();
                if (l > 2) {
                    s.append(t.substring(l - 2));
                } else {
                    if (l == 1) {
                        s.append("0");
                    }
                    s.append(t);
                }
            }

            return s.toString();
        } else {
            return "";
        }
    }

    /**
     * Convert a byte array into a character string using US-ASCII encoding.
     * @param b  Byte array.
     * @return   US-ASCII string representation.
     */
    @SuppressLint("NewApi")
    public static String convertByteArrayToASCIIString(byte[] b) {
        String s = "";

        try {
            s = new String(b, Charset.forName("US-ASCII"));
        } catch (Exception e) {
        }

        return s;
    }

    /**
     * Convert a character string into a byte array using ASCII encoding.
     * @param s  Character string.
     * @return   ASCII byte array representation.
     */
    @SuppressLint("NewApi")
    public static byte[] convertASCIIStringToByteArray(String s) {
        byte[] b = new byte[0];

        try {
            b = s.getBytes(Charset.forName("US-ASCII"));
        } catch (Exception e) {
        }

        return b;
    }

    /**
     * Convert a byte array into a character string using UTF-8 encoding.
     * @param b  Byte array.
     * @return   UTF-8 string representation.
     */
    @SuppressLint("NewApi")
    public static String convertByteArrayToUTF8String(byte[] b) {
        String s = "";

        try {
            s = new String(b, Charset.forName("UTF-8"));
        } catch (Exception e) {
        }

        return s;
    }

    /**
     * Convert a character string into a byte array using UTF-8 encoding.
     * @param s  Character string.
     * @return   UTF-8 byte array representation.
     */
    @SuppressLint("NewApi")
    public static byte[] convertUTF8StringToByteArray(String s) {
        byte[] b = new byte[0];

        try {
            b = s.getBytes(Charset.forName("UTF-8"));
        } catch (Exception e) {
        }

        return b;
    }

    /**
     * Convert a byte array into a character string using UTF-16 encoding.
     * @param b  Byte array.
     * @return   UTF-16 string representation.
     */
    @SuppressLint("NewApi")
    public static String convertByteArrayToUTF16String(byte[] b) {
        String s = "";

        try {
            s = new String(b, Charset.forName("UTF-16"));
        } catch (Exception e) {
        }

        return s;
    }

    /**
     * Convert a character string into a byte array using UTF-16 encoding.
     * @param s  Character string.
     * @return   UTF-16 byte array representation.
     */
    @SuppressLint("NewApi")
    public static byte[] convertUTF16StringToByteArray(String s) {
        byte[] b = new byte[0];

        try {
            b = s.getBytes(Charset.forName("UTF-16"));
        } catch (Exception e) {
        }

        return b;
    }

    /**
     * Convert a string of hexadecimal encoded octets into its byte array representation.
     * @param s  String of hexadecimal encoded octets.
     * @return   Byte array representation.
     */
    public static byte[] convertHexStringToByteArray(String s) {
        final int len = s.length();
        final int rem = len % 2;

        byte[] ret = new byte[len / 2 + rem];

        if (rem != 0) {
            try {
                ret[0] = (byte) (Integer.parseInt(s.substring(0, 1), 16) & 0x00F);
            } catch (Exception e) {
                ret[0] = 0;
            }
        }

        for (int i = rem; i < len; i += 2) {
            try {
                ret[i / 2 + rem] = (byte) (Integer.parseInt(s.substring(i, i + 2), 16) & 0x0FF);
            } catch (Exception e) {
                ret[i / 2 + rem] = 0;
            }
        }

        return ret;
    }

    // CUSTOM CODE

    /**
     * Long to HexString in Big Indian form
     */
    public static String convertLongToHexString(long l) {
        final byte [] tab = new byte[4];
        tab[3] = (byte)l;
        tab[2] = (byte)(l >> 8);
        tab[1] = (byte)(l >> 16);
        tab[0] = (byte)(l >> 24);
        return convertByteArrayToHexString(tab);
    }

    /**
     * Find the correct coding of a URI payload and decode it.<br/>
     * It mainly differentiates UTF-8 and UTF-16.
     * @param payload the ndef message payload
     * @return the decoded and readable payload in a String
     */
    public static String getTextData(byte[] payload)
    {
        int i=payload.length;
        if (i==0) return " ";
        String texteCode = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langageCodeTaille = payload[0] & 0077;
        try
        {
            return new String(payload, langageCodeTaille + 1, payload.length - langageCodeTaille - 1, texteCode);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String removeSpaces(String str) {
        StringBuffer text = new StringBuffer(str);
        int len = text.length();
        for (int i =0; i < len; i++) {
            if (text.charAt(i) == ' ') {
                text.deleteCharAt(i);
                len--;
            }
        }
        return text.toString();
    }

    public static String addSpaces(String str, int interval) {
        StringBuffer text = new StringBuffer(str);
        int nb = text.length();
        for (int i=0, j=0; j < nb; i++,j++) {
            if (j % interval == 0) {
                text.insert(i++, ' ');
            }
        }
        return text.toString();
    }
}