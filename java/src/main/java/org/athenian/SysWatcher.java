package org.athenian;

import com.beust.jcommander.JCommander;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SysWatcher {


    public static void main(final String[] argv) {

        final ServerCliArgs cliArgs = new ServerCliArgs();
        new JCommander(cliArgs, argv);

        final String mqtt_hostname = MqttUtils.getMqttHostname(cliArgs.mqtt_arg);
        final int mqtt_port = MqttUtils.getMqttPort(cliArgs.mqtt_arg);

        final MqttClient client = MqttUtils.createMqttClient(mqtt_hostname, mqtt_port, new SimpleMqttCallback());
        if (client == null)
            return;

        try {
            client.subscribe("$SYS/#",
                             0,
                             new IMqttMessageListener() {
                                 public void messageArrived(String topic, MqttMessage msg) {
                                     System.out.println(String.format("%s : %s", topic, new String(msg.getPayload())));
                                 }
                             });
        }
        catch (MqttException e) {
            System.out.println(String.format("Unable to subscribe to system topics [%s]", e.getMessage()));
        }

    }

}
