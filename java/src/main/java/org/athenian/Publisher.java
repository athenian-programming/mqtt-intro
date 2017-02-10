package org.athenian;

import org.athenian.args.CountArgs;
import org.eclipse.paho.client.mqttv3.*;

import static java.lang.String.format;

public class Publisher {

    public static void main(final String[] argv) throws InterruptedException, MqttException {

        final CountArgs cliArgs = new CountArgs();
        cliArgs.parseArgs(Publisher.class.getName(), argv);

        final String mqtt_hostname = Utils.getMqttHostname(cliArgs.mqtt_arg);
        final int mqtt_port = Utils.getMqttPort(cliArgs.mqtt_arg);

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
            for (int i = 0; i < cliArgs.mqtt_count; i++) {
                // Write a string byte array
                final byte[] bval = ("" + i).getBytes();
                client.publish(cliArgs.mqtt_topic, new MqttMessage(bval));
                // If writing a int, use
                // final byte[] bval = ByteBuffer.allocate(4).putInt(i).array();
                Thread.sleep(1000);
            }
        }
        catch (MqttException e) {
            System.out.println(format("Unable to publish data to %s [%s]", cliArgs.mqtt_topic, e.getMessage()));
        }

        try {
            client.disconnect();
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
