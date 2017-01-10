#!/usr/bin/env python3

import argparse
import logging
import socket
import sys
import time
from threading import Thread

import paho.mqtt.client as paho

from  utils import mqtt_server_info

TOPIC = "topic"


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))
    Thread(target=publish_messages, args=(client, userdata)).start()


def on_disconnect(client, userdata, flags, rc):
    print("Disconnected with result code: {0}".format(rc))


def on_publish(client, userdata, mid):
    print("Published with message id: {0}".format(mid))


def publish_messages(client, userdata):
    for i in range(0, 1000):
        result, mid = client.publish(userdata[TOPIC], payload=i)
        time.sleep(1)


if __name__ == "__main__":
    # Parse CLI args
    parser = argparse.ArgumentParser()
    parser.add_argument("-m", "--mqtt", required=True, help="MQTT server hostname")
    parser.add_argument("-t", "--topic", required=True, help="MQTT topic")
    args = vars(parser.parse_args())

    # Setup logging
    logging.basicConfig(stream=sys.stderr, level=logging.INFO,
                        format="%(asctime)s %(name)-10s %(funcName)-10s():%(lineno)i: %(levelname)-6s %(message)s")

    # Determine MQTT server details
    mqtt_hostname, mqtt_port = mqtt_server_info(args["mqtt"])

    # Create userdata dictionary
    userdata = {TOPIC: args["topic"]}

    # Initialize MQTT client
    client = paho.Client(userdata=userdata)
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_publish = on_publish

    try:
        # Connect to MQTT server
        logging.info("Connecting to MQTT server at {0}:{1}".format(mqtt_hostname, mqtt_port))
        client.connect(mqtt_hostname, port=mqtt_port, keepalive=60)
        client.loop_forever()
    except socket.error:
        logging.error("Cannot connect to MQTT server at: {0}:{1}".format(mqtt_hostname, mqtt_port))
    except KeyboardInterrupt:
        pass

    print("Exiting...")
