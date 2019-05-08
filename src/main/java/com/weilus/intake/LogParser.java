package com.weilus.intake;

import java.util.List;

/**
 * 日志解析
 *
 * @author 刘太全
 * @program intake
 * @date 2019-05-08 09:58
 **/
public interface LogParser<T> {

    T parseLog(String line);

    List<T> parseLog(List<String> lines);

    boolean isErrorLog(String line);

    boolean isExceptionLog(String line);

}
