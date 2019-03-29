package com.weilus.intake;


import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by liutq on 2019/3/21.
 */
public class Application {
    public static final Logger LOGGER = Logger.getLogger(Application.class.getSimpleName());

    public static void main(String[] args){
        Map<String,Map<String,String>> config=loadConfig(args);
        if(config != null){
                config.entrySet().stream()
                        .filter(e->{
                            Map<String,String> transfoer = e.getValue();
                            return transfoer.containsKey("mongo")
                                    && transfoer.containsKey("collection")
                                    && transfoer.containsKey("path")
                                    && transfoer.containsKey("file");
                        })
                        .forEach(t->{
                            Map<String,String> conf = t.getValue();
                            try {
                                LogTransforer transforer = new MongoDBTransforer(conf.get("mongo"),conf.get("collection"));
                                String logDir = conf.get("path");
                                String file = conf.get("file");
                                String source =conf.get("source");
                                DirLinsterer.listener(transforer,logDir,file,source);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }else {
                LOGGER.log(Level.WARNING,"请指定配置文件, shell>java -jar intake.jar /data/intake/etc/intake.yml");
            }
        }


    public static Map<String,Map<String,String>> loadConfig(String[] args){
        Map<String,Map<String,String>> config=null;
        if(args.length > 0 && Files.exists(Paths.get(args[0]))) {
            try (InputStream is = new FileInputStream(Paths.get(args[0]).toFile())){
                config = new Yaml().load(is);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try (InputStream is = ClassLoader.getSystemResourceAsStream("intake.yml")){
                config = new Yaml().load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }
}
