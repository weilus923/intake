# intake
日志采集
```
    java -jar intak.jar <PATH_TO_CONF>/intake.yml
```

编辑intake.yml
```yml
    order:
      path: /data/logs/order      #监听目录
      mongo: mongodb://<user>:<password>@<ip>:<port>/<database>   #提取数据存放DB
      source: order               #DB存储后标记来源,source字段标记
      file: order.log*           #监听文件
      time_format: yyyy-MM-dd HH:mm:ss.SSS  # 时间格式化
      pattern:
        time: "(^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3,3})\\s+" # 正则截取 Time
        level: "(DEBUG|WARN|INFO|ERROR)\\s+"                                      # 正则截取 Level
        selth: "\\[(.*?)\\]"                                                      # 正则截取 selth
        thread: ".*\\[(.*?)\\]"                                                   # 正则截取 Thread
        logger: "\\s+(.*?)\\s+"                                                   # 正则截取 Logger
        message: "\\:\\s+(.*?)$"                                                  # 正则截取 Message

    oauth:
      path: /data/logs/oauth
      mongo: mongodb://weilua:weilus@192.168.198.128:27017/test
      source: oauth
      file: oauth.log*
      time_format: yyyy-MM-dd HH:mm:ss.SSS
      pattern:
        time: "(^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3,3})\\s+"
        level: "(DEBUG|WARN|INFO|ERROR)\\s+"
        selth: "\\[(.*?)\\]"
        thread: ".*\\[(.*?)\\]"
        logger: "\\s+(.*?)\\s+"
        message: "\\:\\s+(.*?)$"
```
