package org.athenian;

import org.athenian.args.TopicCliArgs;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.ByteBuffer;

import static java.lang.String.format;

public class Publisher {

    public static void main(final String[] argv) throws InterruptedException {

        final TopicCliArgs cliArgs = new TopicCliArgs();
        try {
            cliArgs.parseArgs(argv);
        }
        catch (MqttException e) {
            return;
        }

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
        if (client != null) {
            try {
                for (int i = 0; i < 1000; i++) {
                    client.publish(cliArgs.mqtt_topic, new MqttMessage(ByteBuffer.allocate(4).putInt(i).array()));
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
}
