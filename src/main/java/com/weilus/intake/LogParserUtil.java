package com.weilus.intake;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    static final SimpleDateFormat SIMPLE_DATE_FORMAT_SSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    static final SimpleDateFormat SIMPLE_DATE_FORMAT_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parseTime(String line) throws ParseException {
        Matcher matcher = Pattern.compile(REG_DATE_YMDHMS_SSS).matcher(line);
        if(matcher.find()){
            return SIMPLE_DATE_FORMAT_SSS.parse(matcher.group(1));
        }
        matcher = Pattern.compile(REG_DATE_YMDHMS).matcher(line);
        if(matcher.find()){
            return SIMPLE_DATE_FORMAT_HMS.parse(matcher.group(1));
        }
        return null;
    }

    public static String getMatcher(String regex, String source) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if(matcher.find())return matcher.group(1);
        return null;
    }

    public static String parseMsg(String source){
        return source.substring(source.indexOf(":",20)+1);
    }



}
