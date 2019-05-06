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
     */
    public static void listener(LogTransforer transforer,IntakeProperties properties) throws IOException{
        String posDir = properties.getPath().endsWith(File.separator) ? properties.getPath()+"pos/" : properties.getPath()+File.separator+"pos/";
        Path logdir = Paths.get(properties.getPath()),posdir = Paths.get(posDir);
        if(Files.notExists(logdir))Files.createDirectories(logdir);
        if(Files.notExists(posdir))Files.createDirectories(posdir);

        listener(properties.getPath(),(event) -> {
            Thread.currentThread().setName(event.context().toString());
            String logpath = getLogPath(properties.getPath(),event);
            Path logPosPath = Paths.get(posDir+"pos-"+event.context()+ ".data");
            // 摄取目标 被创建
            if(ENTRY_CREATE.name().equals(event.kind().name())){
                LogReader.writeEndPos(logPosPath,0L);
            }
            // 摄取目标 存在新内容写入
            if(ENTRY_MODIFY.name().equals(event.kind().name())){
                if(!LOCKS.containsKey(logpath))LOCKS.put(logpath,false);
                String _source = properties.getSource() != null && properties.getSource().length() >0 ? properties.getSource() : event.context().toString();
                synchronized (LOCKS.get(logpath)){
                    LogReader.readLastLine(Paths.get(logpath), logPosPath, (lines,num) -> transforer.out(lines));
                    LOCKS.put(logpath,false);
                }
            }
        },(event)->{
            Path logPath = Paths.get(getLogPath(properties.getPath(),event));
            if(logPath.toFile().isDirectory())return false;
            String fileName = event.context().toString();
            if(fileName.startsWith("pos-") || fileName.endsWith(".swp") || fileName.endsWith(".swx"))return false;
            if(null != properties.getFile()){
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**\\"+properties.getFile());
                boolean flag =  matcher.matches(logPath);
                if(!flag)LOGGER.info("不符合["+properties.getFile()+"]格式: =>"+logPath.toString());
                return flag;
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
