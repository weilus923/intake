package com.weilus.intake;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

/**
 * conf
 *
 * @author 刘太全
 * @program intake
 * @date 2019-05-06 08:53
 **/
public class IntakeProperties {

    private String path;
    private String file;
    private String source;
    private SimpleDateFormat dateFormat;
    private LinkedHashMap<String,String> pattern;

    public IntakeProperties() {
    }

    public IntakeProperties(String path, String file, String source,String timeFormat, LinkedHashMap<String, String> pattern) {
        this.path = path;
        this.file = file;
        this.dateFormat = new SimpleDateFormat(timeFormat);
        this.source = source;
        this.pattern = pattern;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LinkedHashMap<String, String> getPattern() {
        return pattern;
    }

    public void setPattern(LinkedHashMap<String, String> pattern) {
        this.pattern = pattern;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}
