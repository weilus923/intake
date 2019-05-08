package com.weilus.intake.mongo;


import com.weilus.intake.conf.IntakeProperties;
import com.weilus.intake.LogParser;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 刘太全
 * @program intake
 * @date 2019-05-08 09:59
 **/
public class MongoDocumentParser implements LogParser<Document> {

    public static final Logger LOGGER =Logger.getLogger(MongoDocumentParser.class.getName());

    private IntakeProperties properties;

    private String patterns;

    public MongoDocumentParser(IntakeProperties properties) {
        this.properties = properties;
        this.patterns = properties.getPattern().values().stream().reduce((p1,p2)->p1+p2).get();
    }

    public Document parseLog(String line){
        try {
            Document doc =  new Document();
            Matcher matcher = Pattern.compile(patterns).matcher(line);
            List<String> keys = new ArrayList<>(properties.getPattern().keySet());
            if(matcher.find()){
                    for(int i = 1;i <= matcher.groupCount(); i++){
                        String key = keys.get(i-1),value = matcher.group(i);
                        if("time".equalsIgnoreCase(key))doc.append(key,properties.getDateFormat().parse(value));
                        else  doc.append(key,value);
                    }
                    return doc;
            }else {
//                LOGGER.warning("log at \n"+line+"\n not match :\n"+patterns);
            }
        }catch (Exception e){
            LOGGER.warning(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Document> parseLog(List<String> lines) {
        List<Document> documents = new ArrayList<>();
        for(int i = 0;i<lines.size();i++){
            String l = lines.get(i);
            Document d = parseLog(l);
            Document lastDoc = !documents.isEmpty() ? documents.get(documents.size()-1) : null;
            if(d != null)documents.add(d);
            else if(d == null && lastDoc != null){
                List<String> exstack = (List<String>) lastDoc.get("exception");
                if(exstack == null){
                    exstack = new ArrayList<>();
                }
                exstack.add(l);
                lastDoc.append("exception",exstack);
            }
        }
        return documents;
    }

    public boolean isErrorLog(String line){
        Document doc = parseLog(line);
        return isErrorLog(doc);
    }

    public boolean isErrorLog(Document doc){
        if(doc != null && "ERROR".equalsIgnoreCase(String.valueOf(doc.get("level"))))return true;
        return false;
    }

    public boolean isExceptionLog(String line){
        Document doc = parseLog(line);
        return isExceptionLog(doc);
    }

    public boolean isExceptionLog(Document doc){
        if(doc == null)return true;
        return false;
    }
}
