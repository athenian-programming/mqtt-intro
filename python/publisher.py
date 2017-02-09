#!/usr/bin/env python

import argparse
import time
from threading import Thread

from constants import TOPIC
from mqtt_connection import MqttConnection
from utils import setup_logging, sleep


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))
    Thread(target=publish_messages, args=(client, userdata)).start()


def on_disconnect(client, userdata, rc):
    print("Disconnected with result code: {0}".format(rc))


def on_publish(client, userdata, mid):
    print("Published value to {0} with message id {1}".format(userdata[TOPIC], mid))


def publish_messages(client, userdata):
    for val in range(int(userdata["count"])):
        # Write a string byte array
        bval = str(val).encode('utf-8')
        result, mid = client.publish(userdata[TOPIC], payload=bval, qos=0)
        # To write an int byte array, use: bval = val.to_bytes(4, byteorder="big"):
        # int.to_bytes() requires python3: https://docs.python.org/3/library/stdtypes.html#int.to_bytes
        time.sleep(1)
    userdata["paho.client"].disconnect()


if __name__ == "__main__":
    # Parse CLI args
    parser = argparse.ArgumentParser()
    parser.add_argument("-m", "--mqtt", required=True, help="MQTT broker hostname")
    parser.add_argument("-t", "--topic", required=True, help="MQTT topic")
    parser.add_argument("-c", "--count", default="1000", help="Number of messages to publish")
    args = vars(parser.parse_args())

    # Setup logging
    setup_logging()

    # Setup MQTT client
    mqtt_conn = MqttConnection(args["mqtt"],
                               userdata={TOPIC: args["topic"], "count": args["count"]},
                               on_connect=on_connect,
                               on_disconnect=on_disconnect,
                               on_publish=on_publish)
    mqtt_conn.connect()

    try:
        sleep()
    except KeyboardInterrupt:
        pass
    finally:
        mqtt_conn.disconnect()

    print("Exiting...")
