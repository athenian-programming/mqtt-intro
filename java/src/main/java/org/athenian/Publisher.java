package org.athenian;

import com.beust.jcommander.JCommander;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.ByteBuffer;

public class Publisher {

    public static void main(final String[] argv) throws InterruptedException {

        final TopicCliArgs cliArgs = new TopicCliArgs();

        new JCommander(cliArgs, argv);

        final String mqtt_hostname = MqttUtils.getMqttHostname(cliArgs.mqtt_arg);
        final int mqtt_port = MqttUtils.getMqttPort(cliArgs.mqtt_arg);

        final MqttCallback callback = new SimpleMqttCallback() {
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                super.deliveryComplete(token);
                System.out.println(String.format("Published value to %s with message id %d",
                                                 token.getTopics()[0], token.getMessageId()));
            }
        };

        final MqttClient client = MqttUtils.createMqttClient(mqtt_hostname, mqtt_port, callback);
        if (client == null)
            return;

        try {
            for (int i = 0; i < 1000; i++) {
                client.publish(cliArgs.mqtt_topic, new MqttMessage(ByteBuffer.allocate(4).putInt(i).array()));
                Thread.sleep(1000);
            }
        }
        catch (MqttException e) {
            System.out.println(String.format("Unable to publish data to %s [%s]", cliArgs.mqtt_topic, e.getMessage()));
        }

        try {
            client.disconnect();
        }
        catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
