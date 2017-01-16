#!/usr/bin/env python3

import argparse
import logging
import socket
import sys

import paho.mqtt.client as paho
from  utils import FORMAT_DEFAULT
from  utils import TOPIC
from  utils import mqtt_server_info


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))
    client.subscribe(userdata[TOPIC])


def on_subscribe(client, userdata, mid, granted_qos):
    print("Subscribed with message id: {0} QOS: {1}".format(mid, granted_qos))


def on_message(client, userdata, msg):
    print("{0} : {1}".format(msg.topic, int.from_bytes(msg.payload, byteorder="big")))
    # If i is a string, use: bytes.decode(msg.payload):


if __name__ == "__main__":
    # Parse CLI args
    parser = argparse.ArgumentParser()
    parser.add_argument("-m", "--mqtt", required=True, help="MQTT server hostname")
    parser.add_argument("-t", "--topic", required=True, help="MQTT topic")
    args = vars(parser.parse_args())

    # Setup logging
    logging.basicConfig(stream=sys.stderr, level=logging.INFO, format=FORMAT_DEFAULT)

    # Determine MQTT server details
    mqtt_hostname, mqtt_port = mqtt_server_info(args["mqtt"])

    # Create userdata dictionary
    userdata = {TOPIC: args["topic"]}

    # Initialize MQTT client
    client = paho.Client(userdata=userdata)
    client.on_connect = on_connect
    client.on_message = on_message

    try:
        # Connect to MQT server
        logging.info("Connecting to MQTT server at {0}:{1}...".format(mqtt_hostname, mqtt_port))
        client.connect(mqtt_hostname, port=mqtt_port, keepalive=60)
        client.loop_forever()
    except socket.error:
        logging.error("Cannot connect to MQTT server at: {0}:{1}".format(mqtt_hostname, mqtt_port))
    except KeyboardInterrupt:
        pass

    print("Exiting...")
