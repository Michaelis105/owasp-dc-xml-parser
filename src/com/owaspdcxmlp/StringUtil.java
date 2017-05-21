package com.owaspdcxmlp;

/**
 * Created by micha on 05/20/2017.
 */
public class StringUtil {

    /**
     * Checks if string is either null or empty.
     * There does not exist a method that checks for both a null and zero-length string
     * other than Apache Commons-Lang isEmpty()
     *
     * @param s String
     * @return true if null or empty, false otherwise
     */
    public static boolean isNull(String s) {
        return (null == s || s.isEmpty());
    }
}
