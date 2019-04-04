package com.weilus.intake;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by liutq on 2019/3/22.
 */
public class DirLinsterer {

    public static final Logger LOGGER = Logger.getLogger(DirLinsterer.class.getSimpleName());

    public static ExecutorService executorService = Executors.newFixedThreadPool(50);
    public final static Map<String,Boolean> LOCKS = new ConcurrentHashMap<>();

    /**
     * @param transforer 文件摄取 写出对象
     * @param logDir     文件摄取的目录
     * @param file       文件匹配的parrten  匹配的文件才执行摄取内容写出  eureka.log.*
     * @param source     写出时标记来源
     */
    public static void listener(LogTransforer transforer,String logDir,String file,String source) throws IOException{
        String posDir = logDir.endsWith(File.separator) ? logDir+"pos/" : logDir+File.separator+"pos/";
        Path logdir = Paths.get(logDir),posdir = Paths.get(posDir);
        if(Files.notExists(logdir))Files.createDirectories(logdir);
        if(Files.notExists(posdir))Files.createDirectories(posdir);

        listener(logDir,(event) -> {
            Thread.currentThread().setName(event.context().toString());
            String logpath = getLogPath(logDir,event);
            Path logPosPath = Paths.get(posDir+"pos-"+event.context()+ ".data");
            // 摄取目标 被创建
            if(ENTRY_CREATE.name().equals(event.kind().name())){
                LogReader.writeEndPos(logPosPath,0L);
            }
            // 摄取目标 存在新内容写入
            if(ENTRY_MODIFY.name().equals(event.kind().name())){
                if(!LOCKS.containsKey(logpath))LOCKS.put(logpath,false);
                String _source = source != null && source.length() >0 ? source : event.context().toString();
                synchronized (LOCKS.get(logpath)){
                    LogReader.readLastLine(Paths.get(logpath), logPosPath, (lines) -> transforer.out(lines,_source));
                    LOCKS.put(logpath,false);
                }
            }
        },(event)->{
            if(Paths.get(getLogPath(logDir,event)).toFile().isDirectory())return false;
            String fileName = event.context().toString();
            if(fileName.startsWith("pos-") && fileName.endsWith(".data"))return false;
            if(null != file){
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**\\"+file);
                try {
                    return Files.list(Paths.get(logDir)).anyMatch(path-> matcher.matches(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });
    }

    public static String getLogPath(String logDir,WatchEvent event){
        return logDir.endsWith(File.separator) ? logDir+event.context() : logDir+File.separator+event.context();
    }

    /**
     * 监听目录日志文件
     * @param dir 监听目录
     * @param consumer 消费事件
     * @param predicate 是否执行消费
     * @throws IOException
     */
    public static void listener(String dir, Consumer<WatchEvent> consumer, Predicate<WatchEvent> predicate) throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Paths.get(dir).register(watcher,OVERFLOW,ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
        executorService.execute(()->{
            Thread.currentThread().setName("listenerDir");
            LOGGER.log(Level.INFO,"start listenerDir :"+ dir);
            while(true){
                try {
                    WatchKey watchKey = watcher.take();
                    List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                    for(WatchEvent<?> event : watchEvents){
                        if(predicate.test(event))
                            executorService.execute(()->consumer.accept(event));
                    }
                    watchKey.reset();
                }catch (Exception e){
                    LOGGER.log(Level.FINER,e.getMessage());
                }
            }
        });
    }
}
