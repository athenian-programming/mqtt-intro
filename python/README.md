# Python MQTT Intro

## Package Dependencies

Install the following Python packages: 

* [MQTT](http://mqtt.org) client 
as described [here](http://www.athenian-robotics.org/mqtt-client/)


## Notes 

* The Python code in this repo uses the [Paho Python MQTT client](https://pypi.python.org/pypi/paho-mqtt)

* The [Paho Java MQTT client](https://eclipse.org/paho/clients/java/) uses byte arrays for
message payloads. To maintain interoperability between the java and python messages, the message
payloads are byte arrays.

