package com.weilus.intake;


import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by liutq on 2019/3/21.
 */
public class Application {
    public static final Logger LOGGER = Logger.getLogger(Application.class.getSimpleName());

    public static void main(String[] args) throws IOException {
        Optional<String> mongo = Stream.of(args).filter(arg->arg.startsWith("mongo=")).findFirst();
        if(!mongo.isPresent())LOGGER.log(Level.WARNING,"mongo: 连接uri未设置 例如: mongo=mongodb://user:password@ip:port/database");
        Optional<String> path = Stream.of(args).filter(arg->arg.startsWith("logPath=")).findFirst();
        if(!path.isPresent())LOGGER.log(Level.WARNING,"logPath: 日志目录未设置 例如: logPath=/data/logs");
        if(mongo.isPresent() && path.isPresent()) {
            Optional<String> pospath = Stream.of(args).filter(arg -> arg.startsWith("posPath=")).findFirst();
            String posPath = pospath.isPresent() ? pospath.get().split("=")[1] : System.getProperty("user.dir");
            DirLinsterer.listener(new MongoDBTransforer(mongo.get().split("=")[1]), path.get().split("=")[1],posPath);
        }
    }
}
