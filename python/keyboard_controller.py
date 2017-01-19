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


def on_connect(client, userdata, flags, rc):
    print("Connected with result code: {0}".format(rc))


def on_disconnect(client, userdata, rc):
    print("Disconnected with result code: {0}".format(rc))


def on_publish(client, userdata, mid):
    print("Published value to {0} with message id {1}".format(COMMAND, mid))


def publish_command(cmd):
    result, mid = mqtt_conn.client.publish(COMMAND, payload=cmd.encode('utf-8'))


def on_left_arrow_pressed(event):
    # print("Left arrow pressed")
    publish_command("LEFT")


def on_right_arrow_pressed(event):
    # print("Right arrow pressed")
    publish_command("RIGHT")


def on_up_arrow_pressed(event):
    # print("Up arrow pressed")
    publish_command("FORWARD")


def on_down_arrow_pressed(event):
    # print("Down arrow pressed")
    publish_command("BACKWARD")


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

    # Determine MQTT broker details
    mqtt_hostname, mqtt_port = mqtt_broker_info(args["mqtt"])

    mqtt_conn = MqttConnection(mqtt_hostname, mqtt_port)

    # Setup MQTT callbacks
    mqtt_conn.client.on_connect = on_connect
    mqtt_conn.client.on_disconnect = on_disconnect
    mqtt_conn.client.on_publish = on_publish

    # This will not block
    mqtt_conn.connect()

    root = tk.Tk()
    canvas = tk.Canvas(root, bg="white", width=200, height=150)

    # label = tk.Label(canvas, text='Hello bind world')
    # label.config(bg='red', font=('courier', 20, 'bold'))
    # label.config(height=5, width=20)
    # label.pack(expand=tk.YES, fill=tk.BOTH)

    # For bind() details, see: http://effbot.org/tkinterbook/tkinter-events-and-bindings.htm
    canvas.bind("<Button-1>", on_mouseclick)
    canvas.bind("<Key>", key)
    canvas.bind('<Left>', on_left_arrow_pressed)
    canvas.bind('<Right>', on_right_arrow_pressed)
    canvas.bind('<Up>', on_up_arrow_pressed)
    canvas.bind('<Down>', on_down_arrow_pressed)
    canvas.pack()

    root.mainloop()
