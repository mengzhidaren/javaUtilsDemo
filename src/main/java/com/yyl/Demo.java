package com.yyl;

/**
 * Created by Administrator on 2017/4/27/027.
 */
public class Demo {

    public static void main(String[] args) {
        int x=5;

        int y=x;
        x++;
        System.out.println(y);
        Integer xx=5;
        Integer yy=xx;
        xx++;
        System.out.println(yy);

//        for (int i = 1; i < 10; i++) {
//            System.out.println("i"+i+" "+10%i);
//        }


    }

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
