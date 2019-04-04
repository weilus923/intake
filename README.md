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
      file: order.log.*           #监听文件
    oauth:
      path: /data/logs/oauth
      mongo: mongodb://weilua:weilus@192.168.198.128:27017/test
      source: oauth
      file: oauth.log.*
```
