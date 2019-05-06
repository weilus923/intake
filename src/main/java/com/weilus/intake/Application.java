package com.weilus.intake;


import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by liutq on 2019/3/21.
 */
public class Application {
    public static final Logger LOGGER = Logger.getLogger(Application.class.getSimpleName());

    public static void main(String[] args){
        Map<String,Object> config=loadConfig(args);
        if(config != null){
                config.entrySet().stream()
                        .filter(e->{
                            Map<String,Object> transfoer = (Map<String,Object>) e.getValue();
                            return transfoer.containsKey("mongo")
                                    && transfoer.containsKey("source")
                                    && transfoer.containsKey("path")
                                    && transfoer.containsKey("file")
                                    && transfoer.containsKey("pattern")
                                    && transfoer.containsKey("time_format");
                        })
                        .forEach(e->{
                            Map<String,Object> conf = (Map<String, Object>) e.getValue();
                            try {
                                String mongo = String.valueOf(conf.get("mongo"));
                                String collection = conf.containsKey("collection") ? String.valueOf(conf.get("collection")) : null;
                                String path = String.valueOf(conf.get("path"));
                                String file = String.valueOf(conf.get("file"));
                                String source = String.valueOf(conf.get("source"));
                                String time_format = String.valueOf(conf.get("time_format"));
                                LinkedHashMap<String,String> pattern = (LinkedHashMap<String, String>) conf.get("pattern");
                                IntakeProperties properties = new IntakeProperties(path,file,source,time_format,pattern);
                                LogTransforer transforer = new MongoDBTransforer(mongo,collection,properties);
                                DirLinsterer.listener(transforer,properties);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });
            }else {
                LOGGER.log(Level.WARNING,"请指定配置文件, shell>java -jar intake.jar /data/intake/etc/intake.yml");
            }
        }


    public static LinkedHashMap<String,Object> loadConfig(String[] args){
        LinkedHashMap<String,Object> config=null;
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
