package com.weilus.intake;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liutq on 2019/3/22.
 */
public class LogParserUtil {

    public static final String REG_DATE_YMDHMS_SSS = "(^\\d{4}(\\-\\d{2})+[\\s](\\d{2}\\:)+\\d{2}\\.\\d{0,3})";
    public static final String REG_DATE_YMDHMS = "(^\\d{4}(\\-\\d{2})+[\\s](\\d{2}\\:)+\\d{2})";
    public static final String REG_LEVEL = "(DEBUG|WARN|INFO|ERROR)";
    public static final String REG_SELTH = "(\\[(.+?)\\])";
    public static final String REG_CLZZ = "((\\w+\\.){2,}\\w+)";


    public static String getMatcher(String regex, String source) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if(matcher.find())return matcher.group(1);
        return null;
    }

    public static String parseMsg(String source){
        return source.substring(source.indexOf(":",20)+1);
    }
}
