# Java MQTT Intro

## Building Jars

```bash
$ make build
```

## Executing Jars

### SysWatcher.java 
```bash
$ java -jar target/syswatcher-jar-with-dependencies.jar --mqtt localhost
```

### Subscriber.java 

```bash
$ java -jar target/subscriber-jar-with-dependencies.jar --mqtt localhost --topic /test
```

### Publisher.java 

```bash
$ java -jar target/publisher-jar-with-dependencies.jar --mqtt localhost --topic /test --count 10
```

