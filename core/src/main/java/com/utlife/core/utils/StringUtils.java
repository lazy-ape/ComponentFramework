package com.utlife.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuqiang on 2016/12/12.
 */

public class StringUtils {

    public static boolean isNotEmpty(String str) {
        return ((str != null) && (str.trim().length() > 0));
    }

    public static boolean isEmpty(String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static boolean isMobileNo(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    //连接字符串
    public static String join(Object[] array,String separator){
        if(separator == null){
            separator = ",";
        }
        if(array == null || array.length == 0){
            return "";
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0 ; i < array.length ; i++ ){
            if(i > 0){
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return  buf.toString();
    }

}
