package org.athenian;

import org.athenian.args.BrokerArgs;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.String.format;

public class SysWatcher {

    public static void main(final String[] argv) throws MqttException {

        final BrokerArgs cliArgs = new BrokerArgs();
        cliArgs.parseArgs(SysWatcher.class.getName(), argv);

        final String mqtt_hostname = Utils.getMqttHostname(cliArgs.mqtt_arg);
        final int mqtt_port = Utils.getMqttPort(cliArgs.mqtt_arg);

        final MqttClient client = Utils.createMqttClient(mqtt_hostname, mqtt_port, new BaseMqttCallback());
        if (client == null)
            return;

        try {
            client.subscribe("$SYS/#",
                             0,
                             new IMqttMessageListener() {
                                 @Override
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
