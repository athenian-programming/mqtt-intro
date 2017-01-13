# Python MQTT Intro

## Notes 

* The [Paho Java MQTT client](https://eclipse.org/paho/clients/java/) uses byte arrays for
message payloads. To maintain interoperability between the java and python messages, code in this module 
uses byte arrays for message payloads.

* The *subscribers.py* and *publisher.py* programs require python3 because they use 
[int.to_bytes() and int.from_bytes()](https://docs.python.org/3/library/stdtypes.html#int.to_bytes) to 
read/write byte arrays. 

* Code in this module uses the [Paho Python MQTT client](https://pypi.python.org/pypi/paho-mqtt)