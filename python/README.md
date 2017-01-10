# MQTT Intro

## Installation 

### OSX

To install mosquitto:
```bash
$ brew install mosquitto
```

To add mosquitto to launchd:
```bash
$ brew services start mosquitto
```
  
If you don't want to run mosquitto as a background service:
```bash
$ /usr/local/sbin/mosquitto -c /usr/local/etc/mosquitto/mosquitto.conf
```

### Docker

To run mosquitto as a docker service:
```bash
$ docker run -ti -p 1883:1883 -p 9001:9001 toke/mosquitto
```

Image details are [here](https://github.com/toke/docker-mosquitto)

### Raspberry Pi

```bash
$ sudo apt-get install mosquitto
$ sudo apt-get install mosquitto-clients
```

## Testing from CLI

### Subscribe

```bash
$ mosquitto_sub -d -h localhost -t /testnode
Client mosqsub/27524-pleiku.lo sending CONNECT
Client mosqsub/27524-pleiku.lo received CONNACK
Client mosqsub/27524-pleiku.lo sending SUBSCRIBE (Mid: 1, Topic: /testnode, QoS: 0)
Client mosqsub/27524-pleiku.lo received SUBACK
Subscribed (mid: 1): 0
```

### Publish

```bash
$ mosquitto_pub -d -h localhost -m "simple val" -t /testnode
Client mosqpub/27472-pleiku.lo sending CONNECT
Client mosqpub/27472-pleiku.lo received CONNACK
Client mosqpub/27472-pleiku.lo sending PUBLISH (d0, q0, r0, m1, '/testnode', ... (10 bytes))
Client mosqpub/27472-pleiku.lo sending DISCONNECT
```


