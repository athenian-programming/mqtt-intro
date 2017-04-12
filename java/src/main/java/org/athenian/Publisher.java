package org.athenian;

import org.athenian.args.CountArgs;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.String.format;

public class Publisher {

    public static void main(final String[] argv)
        throws InterruptedException, MqttException {

        final CountArgs countArgs = new CountArgs();
        countArgs.parseArgs(Publisher.class.getName(), argv);

        final String mqtt_hostname = Utils.getMqttHostname(countArgs.mqtt_arg);
        final int mqtt_port = Utils.getMqttPort(countArgs.mqtt_arg);

        final MqttCallback callback = new BaseMqttCallback() {
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                super.deliveryComplete(token);
                System.out.println(format("Published value to %s with message id %d",
                                          token.getTopics()[0], token.getMessageId()));
            }
        };

        final MqttClient client = Utils.createMqttClient(mqtt_hostname, mqtt_port, callback);
        if (client == null)
            return;

        try {
            for (int i = 0; i < countArgs.mqtt_count; i++) {
                // Write a string byte array
                final byte[] bval = ("" + i).getBytes();
                client.publish(countArgs.mqtt_topic, new MqttMessage(bval));
                // If writing a int, use
                // final byte[] bval = ByteBuffer.allocate(4).putInt(i).array();
                Thread.sleep(1000);
            }
        }
        catch (MqttException e) {
            System.out.println(format("Unable to publish data to %s [%s]", countArgs.mqtt_topic, e.getMessage()));
        }

        try {
            client.disconnect();
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
