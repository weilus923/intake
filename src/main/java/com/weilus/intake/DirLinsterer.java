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

    public static void listener(LogTransforer transforer,String logDir,String posDir) throws IOException{
        Path posdir = Paths.get(posDir),logdir = Paths.get(logDir);
        if(Files.notExists(posdir))Files.createDirectories(posdir);
        if(Files.notExists(logdir))Files.createDirectories(logdir);
        listener(logDir,(event) -> {
            Thread.currentThread().setName(event.context().toString());
            LOGGER.info("["+event.context()+"]文件发生了["+event.kind()+"]事件");
            String logpath = logDir.endsWith(File.separator) ? logDir+event.context() : logDir+File.separator+event.context();
            String logpospath =posDir.endsWith(File.separator) ? posDir+event.context()+ ".pos" : posDir+File.separator+event.context()+ ".pos";
            Path logPosPath = Paths.get(logpospath);
            if(ENTRY_CREATE.name().equals(event.kind().name())){
                LogReader.writeEndPos(logPosPath,0L);
            }
            if(ENTRY_MODIFY.name().equals(event.kind().name())){
                if(!LOCKS.containsKey(logpath))LOCKS.put(logpath,false);
                synchronized (LOCKS.get(logpath)){
                    LogReader.readLastLine(Paths.get(logpath), logPosPath, (lines) -> transforer.out(lines));
                    LOCKS.put(logpath,false);
                }
            }
        });
    }

    /**
     * 监听目录日志文件
     * @param dir
     * @param consumer
     * @throws IOException
     * @throws InterruptedException
     */
    public static void listener(String dir,Consumer<WatchEvent> consumer) throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Paths.get(dir).register(watcher,OVERFLOW,ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
        while(true){
            try {
                WatchKey watchKey = watcher.take();
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                for(WatchEvent<?> event : watchEvents){
                    executorService.execute(()->consumer.accept(event));
                }
                watchKey.reset();
            }catch (Exception e){
                LOGGER.log(Level.FINER,e.getMessage());
            }
        }
    }
}
