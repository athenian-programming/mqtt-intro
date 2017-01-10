package org.athenian;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.eclipse.paho.client.mqttv3.*;

public class SysWatcher implements MqttCallback {

    @Parameter(names = {"-m", "--mqtt"}, required = true, description = "MQTT server hostname")
    public String mqtt_arg;

    public static void main(final String[] args) {

        final SysWatcher sysWatcher = new SysWatcher();
        new JCommander(sysWatcher, args);

        final String mqtt_hostname = MqttUtils.getMqttHostname(sysWatcher.mqtt_arg);
        final int mqtt_port = MqttUtils.getMqttPort(sysWatcher.mqtt_arg);

        try {
            System.out.println(String.format("Connecting to MQTT server at %s:%d...", mqtt_hostname, mqtt_port));
            final String clientId = MqttClient.generateClientId();
            final MqttClient client = new MqttClient(String.format("tcp://%s:%d", mqtt_hostname, mqtt_port), clientId);
            client.setCallback(sysWatcher);
            client.connect();
            System.out.println(String.format("Connected to %s:%d", mqtt_hostname, mqtt_port));

            client.subscribe("$SYS/#",
                             new IMqttMessageListener() {
                                 public void messageArrived(String topic, MqttMessage msg) {
                                     System.out.println(String.format("%s : %s", topic, new String(msg.getPayload())));
                                 }
                             });
        } catch (MqttException e) {
            System.out.println(String.format("Cannot connect to MQTT server at: %s:%d [%s]",
                                             mqtt_hostname, mqtt_port, e.getMessage()));
        }
    }

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT server lost");
    }

    public void messageArrived(String topic, MqttMessage msg) throws Exception {
        // Empty
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // Empty
    }

}
