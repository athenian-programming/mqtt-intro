#!/usr/bin/env python3

import argparse
import logging
import socket
import sys

from mqtt_connection import MqttConnection
from utils import FORMAT_DEFAULT
from utils import is_python3
from utils import mqtt_broker_info

if is_python3():
    import tkinter as tk
else:
    import Tkinter as tk

COMMAND = "/roborio/keyboard/command"
LEFT = "LEFT"
RIGHT = "RIGHT"
FORWARD = "FORWARD"
BACKWARD = "BACKWARD"


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))


def on_disconnect(client, userdata, rc):
    print("Disconnected with result code: {0}".format(rc))


def on_publish(client, userdata, mid):
    print("Published value to {0} with message id {1}".format(COMMAND, mid))


def publish_command(cmd):
    result, mid = mqtt_conn.client.publish(COMMAND, payload=cmd.encode('utf-8'))


def on_left_arrow_pressed(event):
    label["text"] = LEFT
    publish_command(LEFT)


def on_right_arrow_pressed(event):
    label["text"] = RIGHT
    publish_command(RIGHT)


def on_up_arrow_pressed(event):
    label["text"] = FORWARD
    publish_command(FORWARD)


def on_down_arrow_pressed(event):
    label["text"] = BACKWARD
    publish_command(BACKWARD)


def key(event):
    key_clicked = repr(event.char)
    label["text"] = "Pressed {0}".format(key_clicked)
    if key_clicked == "'q'":
        sys.exit()


def on_mouseclick(event):
    root.focus_set()
    label["text"] = "Clicked at {0},{1}".format(event.x, event.y)


def connect_to_mqtt(client, args):
    # Setup MQTT callbacks
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect
    client.on_publish = on_publish

    # Determine MQTT broker details
    mqtt_hostname, mqtt_port = mqtt_broker_info(args["mqtt"])

    try:
        # Connect to MQTT broker
        logging.info("Connecting to MQTT broker at {0}:{1}...".format(mqtt_hostname, mqtt_port))
        client.connect(mqtt_hostname, port=mqtt_port, keepalive=60)
        client.loop_forever()
    except socket.error:
        logging.error("Cannot connect to MQTT broker at: {0}:{1}".format(mqtt_hostname, mqtt_port))
        exit()


if __name__ == "__main__":
    # Parse CLI args
    parser = argparse.ArgumentParser()
    parser.add_argument("-m", "--mqtt", required=True, help="MQTT broker hostname")
    args = vars(parser.parse_args())

    # Setup logging
    logging.basicConfig(stream=sys.stderr, level=logging.INFO, format=FORMAT_DEFAULT)

    # Create MQTT connection
    mqtt_conn = MqttConnection(*mqtt_broker_info(args["mqtt"]))

    # Setup MQTT callbacks
    mqtt_conn.client.on_connect = on_connect
    mqtt_conn.client.on_disconnect = on_disconnect
    mqtt_conn.client.on_publish = on_publish

    # This will not block
    mqtt_conn.connect()

    root = tk.Tk()
    # For bind() details, see: http://effbot.org/tkinterbook/tkinter-events-and-bindings.htm
    root.bind("<Button-1>", on_mouseclick)
    root.bind("<Key>", key)
    root.bind('<Left>', on_left_arrow_pressed)
    root.bind('<Right>', on_right_arrow_pressed)
    root.bind('<Up>', on_up_arrow_pressed)
    root.bind('<Down>', on_down_arrow_pressed)

    canvas = tk.Canvas(root, bg="white", width=200, height=150)
    label = tk.Label(canvas,
                     text='',
                     bg='red',
                     font=('courier', 20, 'bold'),
                     height=5,
                     width=20)
    label.pack(expand=tk.YES, fill=tk.BOTH)
    canvas.pack()

    root.mainloop()