package com.weilus.intake;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liutq on 2019/3/21.
 */
public class MongoDBTransforer implements LogTransforer{
    public static final Logger LOGGER = Logger.getLogger(MongoDBTransforer.class.getName());
    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private String collection = "intake";
    private IntakeProperties properties;

    public MongoDBTransforer() {
    }

    public MongoDBTransforer(String uri, String collection,IntakeProperties properties) {
        this.mongoClientURI = new MongoClientURI(uri);
        this.mongoClient = new MongoClient(mongoClientURI);
        if(collection != null && collection.length() > 0)this.collection = collection;
        this.properties = properties;
    }

    @Override
    public void out(List<String> lines) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
        MongoCollection collection = mongoDatabase.getCollection(this.collection);
        List<Document> list = trans(lines);
        if(list.size() > 0)collection.insertMany(list);
    }


    public List<Document> trans(List<String> lines){
        List<Document> result = new ArrayList<>();
        if(null != lines) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Document doc = trans(line);
                if(doc == null)continue;
                else doc.append("source", properties.getSource());
                if (isErrorDoc(doc)) {
                    String nextLine;
                    List<String> stacktrace = new ArrayList<>();
                    getStacktrace:
                    do {
                        if(i++ < lines.size()) {
                            nextLine = lines.get(i);
                            if (LogReader.isExceptionStacktrace(nextLine)) stacktrace.add(nextLine);
                            else {
                                i--;
                                break getStacktrace;
                            }
                        }else break;
                    } while (true);
                    doc.append("exception", stacktrace);
                }
                result.add(doc);
            }
        }
        return result;
    }

    public Document trans(String line){
            Document doc =  new Document();
            String patterns = properties.getPattern().values().stream().reduce((p1,p2)->p1+p2).get();
            Matcher matcher = Pattern.compile(patterns).matcher(line);
            List<String> keys = new ArrayList<>(properties.getPattern().keySet());
            if(matcher.find()){
                try {
                    for(int i = 1;i <= matcher.groupCount(); i++){
                        String key = keys.get(i-1),value = matcher.group(i);
                        if("time".equalsIgnoreCase(key))doc.append(key,properties.getDateFormat().parse(value));
                        else  doc.append(key,value);
                    }
                    return doc;
                }catch (Exception e){
                    LOGGER.warning("log at \n"+line+"\n not match :\n"+patterns);
                }
            }
            return null;
    }


    private boolean isErrorDoc(Document doc){
        return "ERROR".equalsIgnoreCase(doc.get("level")+"");
    }

}
