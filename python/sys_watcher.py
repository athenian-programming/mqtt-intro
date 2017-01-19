#!/usr/bin/env python3

import argparse
import logging
import socket
import sys

import paho.mqtt.client as paho

from  utils import FORMAT_DEFAULT
from  utils import mqtt_broker_info


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))
    # Subscribe to internal broker messages
    client.subscribe("$SYS/#")


def on_disconnect(client, userdata, rc):
    print("Disconnected with result code: {0}".format(rc))


def on_message(client, userdata, msg):
    print("{0} : {1}".format(msg.topic, msg.payload))


if __name__ == "__main__":
    # Parse CLI args
    parser = argparse.ArgumentParser()
    parser.add_argument("-m", "--mqtt", required=True, help="MQTT broker hostname")
    args = vars(parser.parse_args())

    # Setup logging
    logging.basicConfig(stream=sys.stderr, level=logging.INFO, format=FORMAT_DEFAULT)

    # Determine MQTT broker details
    mqtt_hostname, mqtt_port = mqtt_broker_info(args["mqtt"])

    # Initialize MQTT client
    client = paho.Client()

    # Setup MQTT callbacks
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_message = on_message

    try:
        # Connect to MQTT broker
        logging.info("Connecting to MQTT broker at {0}:{1}...".format(mqtt_hostname, mqtt_port))
        client.connect(mqtt_hostname, port=mqtt_port, keepalive=60)
        client.loop_forever()
    except socket.error:
        logging.error("Cannot connect to MQTT broker at: {0}:{1}".format(mqtt_hostname, mqtt_port))
    except KeyboardInterrupt:
        pass

    print("Exiting...")
