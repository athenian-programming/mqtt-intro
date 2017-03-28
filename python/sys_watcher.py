#!/usr/bin/env python

import argparse

from mqtt_connection import MqttConnection
from utils import setup_logging
from utils import sleep


def on_connect(mqtt_client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))
    # Subscribe to internal broker messages
    mqtt_client.subscribe("$SYS/#")


def on_disconnect(mqtt_client, userdata, rc):
    print("Disconnected with result code: {0}".format(rc))


def on_message(mqtt_client, userdata, msg):
    print("{0} : {1}".format(msg.topic, msg.payload))


if __name__ == "__main__":
    # Parse CLI args
    parser = argparse.ArgumentParser()
    parser.add_argument("-m", "--mqtt", required=True, help="MQTT broker hostname")
    args = vars(parser.parse_args())

    # Setup logging
    setup_logging()

    # Setup MQTT client
    mqtt_conn = MqttConnection(args["mqtt"],
                               on_connect=on_connect,
                               on_disconnect=on_disconnect,
                               on_message=on_message)
    mqtt_conn.connect()

    try:
        sleep()
    except KeyboardInterrupt:
        pass
    finally:
        mqtt_conn.disconnect()

    print("Exiting...")
