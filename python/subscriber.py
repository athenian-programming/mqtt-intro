#!/usr/bin/env python3

import paho.mqtt.client as paho


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))
    client.subscribe("/test/#")


def on_subscribe(client, userdata, mid, granted_qos):
    print("Subscribed with message id: {0} QOS: {1}".format(mid, granted_qos))


def on_message(client, userdata, msg):
    print("{0} {1}".format(msg.topic, msg.payload))


if __name__ == "__main__":
    client = paho.Client()
    client.on_connect = on_connect
    client.on_message = on_message
    client.on_subscribe = on_subscribe
    client.connect("localhost", 1883, 60)
    client.loop_forever()
