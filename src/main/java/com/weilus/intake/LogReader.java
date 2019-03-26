package com.weilus.intake;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by liutq on 2019/3/22.
 */
public class LogReader {
    static Logger LOGGER = Logger.getLogger(LogReader.class.getSimpleName());
    public static void readLastLine(Path log_path, Path logpospath, Consumer<List<String>> consumer){
        LOGGER.info("读取日志文件: "+log_path.getFileName());
        long start = readStartPos(logpospath);
        long end = start;
        try (
                BufferedReader reader = Files.newBufferedReader(log_path)
        )
        {
            List<String> lines;
            reader.skip(start);
            do {
                lines = reader.lines().limit(100).collect(Collectors.toList());
                Optional<Integer> op = lines.stream().map(line->line.length()+2).reduce((l1, l2)->l1+l2);
                end = op.isPresent() ? end + op.get() : end;
                if (lines.size() > 0) consumer.accept(lines);
            }while (lines.size() > 0);
            if (end > start)writeEndPos(logpospath, end);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeEndPos(Path logpospath,Long end){
        if(Files.notExists(logpospath)){
            LOGGER.info("文件不存在: "+logpospath);
            try {
                Files.createFile(logpospath);
                LOGGER.info("创建文件: "+logpospath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            LOGGER.info("写入文件: "+logpospath+"  >>"+end);
            Files.write(logpospath,String.valueOf(end).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Long readStartPos(Path logpospath){
        long start = 0 ;
        try {
            if(Files.exists(logpospath)) {
                Optional<String> op = Files.lines(logpospath).findFirst();
                start = op.isPresent() ? Long.valueOf(op.get()) : start;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return start;
    }
}
