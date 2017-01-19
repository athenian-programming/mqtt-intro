import argparse
import logging
import socket
import sys
from threading import Thread

import paho.mqtt.client as paho

from utils import FORMAT_DEFAULT
from utils import is_python3
from utils import mqtt_broker_info

if is_python3():
    import tkinter as tk
else:
    import Tkinter as tk

COMMAND = "/roborio/keyboard/command"


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))


def on_disconnect(client, userdata, rc):
    print("Disconnected with result code: {0}".format(rc))


def on_publish(client, userdata, mid):
    print("Published value to {0} with message id {1}".format(COMMAND, mid))


def publish_command(cmd):
    result, mid = client.publish(COMMAND, payload=cmd.encode('utf-8'))


def on_left_arrow_pressed(event):
    # print("Left arrow pressed")
    publish_command("LEFT")


def on_right_arrow_pressed(event):
    # print("Right arrow pressed")
    publish_command("RIGHT")


def on_up_arrow_pressed(event):
    # print("Up arrow pressed")
    publish_command("UP")


def on_down_arrow_pressed(event):
    # print("Down arrow pressed")
    publish_command("DOWN")


def key(event):
    key_clicked = repr(event.char)
    print("pressed " + key_clicked)


def on_mouseclick(event):
    canvas.focus_set()
    print("clicked at", event.x, event.y)


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

    connected = False

    # Initialize MQTT client
    client = paho.Client()

    # Connect to MQTT in a thread
    Thread(target=connect_to_mqtt, args=(client, args)).start()

    root = tk.Tk()
    canvas = tk.Canvas(root, bg="white", width=200, height=300)

    # For bind() details, see: http://effbot.org/tkinterbook/tkinter-events-and-bindings.htm
    canvas.bind("<Button-1>", on_mouseclick)
    canvas.bind("<Key>", key)
    canvas.bind('<Left>', on_left_arrow_pressed)
    canvas.bind('<Right>', on_right_arrow_pressed)
    canvas.bind('<Up>', on_up_arrow_pressed)
    canvas.bind('<Down>', on_down_arrow_pressed)
    canvas.pack()

    root.mainloop()
