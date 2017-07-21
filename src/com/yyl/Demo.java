package com.yyl;

import java.util.UUID;

/**
 * Created by Administrator on 2017/4/27/027.
 */
public class Demo {

    public static void main(String[] args) {
        System.out.println("hallo world");


        UUID uuid = UUID.randomUUID();
        System.out.println("uuid=" + uuid.toString());
        uuid = UUID.nameUUIDFromBytes("abc".getBytes());
        System.out.println("uuid   abc=" + uuid.toString());
        uuid = UUID.nameUUIDFromBytes("abcd".getBytes());
        System.out.println("uuid   abc=" + uuid.toString());
        uuid = UUID.nameUUIDFromBytes("abcd".getBytes());
        System.out.println("uuid   abc=" + uuid.toString());
    }

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
