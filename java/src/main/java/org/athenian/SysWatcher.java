package org.athenian;

import org.athenian.args.ServerCliArgs;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.String.format;

public class SysWatcher {

    public static void main(final String[] argv) {

        final ServerCliArgs cliArgs = new ServerCliArgs();
        cliArgs.parseArgs(argv);

        final String mqtt_hostname = MqttUtils.getMqttHostname(cliArgs.mqtt_arg);
        final int mqtt_port = MqttUtils.getMqttPort(cliArgs.mqtt_arg);

        final MqttClient client = MqttUtils.createMqttClient(mqtt_hostname, mqtt_port, new BaseMqttCallback());
        if (client != null) {
            try {
                client.subscribe("$SYS/#",
                                 0,
                                 new IMqttMessageListener() {
                                     public void messageArrived(String topic, MqttMessage msg) {
                                         System.out.println(format("%s : %s", topic, new String(msg.getPayload())));
                                     }
                                 });
            }
            catch (MqttException e) {
                System.out.println(format("Unable to subscribe to system topics [%s]", e.getMessage()));
            }
        }
    }
}
