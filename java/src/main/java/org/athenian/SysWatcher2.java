package org.athenian;

import org.athenian.args.BrokerArgs;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.String.format;

public class SysWatcher2 {

  public static void main(final String[] argv) {

    final BrokerArgs cliArgs = new BrokerArgs();
    try {
      cliArgs.parseArgs(SysWatcher2.class.getName(), argv);
    }
    catch (MqttException e) {
      return;
    }

    final String mqtt_hostname = Utils.getMqttHostname(cliArgs.mqtt_arg);
    final int mqtt_port = Utils.getMqttPort(cliArgs.mqtt_arg);

    final MqttClient client = Utils.createMqttClient(mqtt_hostname, mqtt_port, new BaseMqttCallback());
    if (client != null) {
      try {
        client.subscribe("/logging",
                         0,
                         new IMqttMessageListener() {
                           @Override
                           public void messageArrived(String topic, MqttMessage msg) {
                             String val = new String(msg.getPayload());
                             System.out.println(format("Logging %s : %s", topic, val));
                           }
                         });
        client.subscribe("/roborio/#",
                         0,
                         new IMqttMessageListener() {
                           @Override
                           public void messageArrived(String topic, MqttMessage msg) {
                             String val = new String(msg.getPayload());
                             System.out.println(format("roborio %s : %s", topic, val));
                           }
                         });
      }
      catch (MqttException e) {
        System.out.println(format("Unable to subscribe to system topics [%s]", e.getMessage()));
      }
    }
  }
}
