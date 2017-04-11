package org.athenian;

import org.athenian.args.TopicArgs;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.String.format;

public class Subscriber {

    public static void main(final String[] argv) throws InterruptedException, MqttException {
        final TopicArgs topicArgs = new TopicArgs();
        topicArgs.parseArgs(Subscriber.class.getName(), argv);

        final MqttClient client = Utils.createMqttClient(Utils.getMqttHostname(topicArgs.mqtt_arg),
                                                         Utils.getMqttPort(topicArgs.mqtt_arg),
                                                         new BaseMqttCallback());
        if (client == null)
            return;

        try {
            client.subscribe(topicArgs.mqtt_topic,
                             new IMqttMessageListener() {
                                 @Override
                                 public void messageArrived(String topic, MqttMessage msg) throws Exception {
                                     // Write a string byte array
                                     final String val = new String(msg.getPayload());
                                     // If reading an int, use\:
                                     // final int val = ByteBuffer.wrap(msg.getPayload()).getInt();
                                     System.out.println(format("%s : %s", topic, val));
                                 }
                             });
        }
        catch (MqttException e) {
            System.out.println(format("Unable to subscribe to %s [%s]", topicArgs.mqtt_topic, e.getMessage()));
        }
    }
}
