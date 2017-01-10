# Python MQTT Intro

## Notes 

* The [Paho Java MQTT client](https://eclipse.org/paho/clients/java/) uses byte arrays for
message payloads. To maintain interoperability between the java and python messages, code in this module 
uses byte arrays for message payloads.

* subscribers.py and publisher.py require python3 because they use 
[int.to_byted and int.from_bytes](https://docs.python.org/3/library/stdtypes.html#int.to_bytes) to 
deal with byte arrays. 

