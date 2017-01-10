package org.athenian.args;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class ServerCliArgs {
    @Parameter(names = {"-m", "--mqtt"}, required = true, description = "MQTT server hostname")
    public String mqtt_arg;

    public void parseArgs(final String[] argv) {
        new JCommander(this, argv);
    }
}
