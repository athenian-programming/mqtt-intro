package org.athenian.args;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.eclipse.paho.client.mqttv3.MqttException;

public class ServerCliArgs {
    @Parameter(names = {"-m", "--mqtt"}, required = true, description = "MQTT server hostname")
    public String mqtt_arg;

    public void parseArgs(final String[] argv) throws MqttException {
        try {
            new JCommander(this, argv);
        }
        catch (ParameterException e) {
            System.out.println(e.getMessage());
            throw new MqttException(e);
        }
    }
}
