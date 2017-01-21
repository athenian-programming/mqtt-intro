#!/usr/bin/env python3

import argparse
import logging
import socket
import time
from threading import Thread

import paho.mqtt.client as paho

from common_constants import LOGGING_ARGS
from common_constants import TOPIC
from common_utils import mqtt_broker_info

CLIENT = "client"


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))
    Thread(target=publish_messages, args=(client, userdata)).start()


def on_disconnect(client, userdata, rc):
    print("Disconnected with result code: {0}".format(rc))


def on_publish(client, userdata, mid):
    print("Published value to {0} with message id {1}".format(userdata[TOPIC], mid))


def publish_messages(client, userdata):
    for val in range(int(userdata["count"])):
        result, mid = client.publish(userdata[TOPIC], payload=val.to_bytes(4, byteorder="big"))
        # If i is a string, use: val.encode('utf-8'):
        time.sleep(1)
    userdata[CLIENT].disconnect()


if __name__ == "__main__":
    # Parse CLI args
    parser = argparse.ArgumentParser()
    parser.add_argument("-m", "--mqtt", required=True, help="MQTT broker hostname")
    parser.add_argument("-t", "--topic", required=True, help="MQTT topic")
    parser.add_argument("-c", "--count", default="1000", help="Number of messages to publish")
    args = vars(parser.parse_args())

    # Setup logging
    logging.basicConfig(**LOGGING_ARGS)

    # Create userdata dictionary
    userdata = {TOPIC: args["topic"], "count": args["count"]}

    # Initialize MQTT client
    client = paho.Client(userdata=userdata)

    # Add client to userdata
    userdata[CLIENT] = client

    # Setup MQTT callbacks
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_publish = on_publish

    # Determine MQTT broker details
    mqtt_hostname, mqtt_port = mqtt_broker_info(args["mqtt"])

    try:
        # Connect to MQTT broker
        logging.info("Connecting to MQTT broker {0}:{1}...".format(mqtt_hostname, mqtt_port))
        client.connect(mqtt_hostname, port=mqtt_port, keepalive=60)
        client.loop_forever()
    except socket.error:
        logging.error("Cannot connect to MQTT broker {0}:{1}".format(mqtt_hostname, mqtt_port))
    except KeyboardInterrupt:
        pass

    print("Exiting...")
