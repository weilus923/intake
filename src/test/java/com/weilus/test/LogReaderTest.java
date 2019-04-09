package com.weilus.test;

import com.weilus.intake.LogReader;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by liutq on 2019/4/9.
 */
public class LogReaderTest {
    public static void main(String[] args) {
        Path logPath = Paths.get("D:\\tmp\\logs\\dms.log");
        Path logpospath =Paths.get("D:\\tmp\\logs\\dms.log.data");
        LogReader.readLastLine(logPath,logpospath,(lines,num)->{
            if(num%2 == 1)System.err.println("第"+num+"批      "+lines);
            else System.out.println("第"+num+"批      "+lines);
        });
    }
}
