package com.weilus.intake;


import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by liutq on 2019/3/21.
 */
public class Application {
    public static final Logger LOGGER = Logger.getLogger(Application.class.getSimpleName());

    public static void main(String[] args) throws IOException {
        if(args.length > 0){
           Path conf = Paths.get(args[0]);
            if(Files.exists(conf)){
                Yaml yaml = new Yaml();
                Map<String,Map<String,String>> config = yaml.load(new FileInputStream(conf.toFile()));
                config.entrySet().stream()
                        .filter(e->{
                            Map<String,String> transfoer = e.getValue();
                            return transfoer.containsKey("mongo")
                                    && transfoer.containsKey("collection")
                                    && transfoer.containsKey("path")
                                    && transfoer.containsKey("file");
                        })
                        .forEach(t->{
                            Map<String,String> transfoer = t.getValue();
                            try {
                                LogTransforer transforer = new MongoDBTransforer(transfoer.get("mongo"),transfoer.get("collection"));
                                DirLinsterer.listener(transforer,transfoer.get("path"),transfoer.get("file"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }else {
                LOGGER.log(Level.WARNING,"配置文件不存在");
            }
        }else {
            LOGGER.log(Level.WARNING,"请指定配置文件, shell>java -jar intake.jar /data/intake/etc/intake.yml");
        }
    }
}
