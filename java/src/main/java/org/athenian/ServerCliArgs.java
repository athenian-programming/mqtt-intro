package org.athenian;

import com.beust.jcommander.Parameter;

public class ServerCliArgs {
    @Parameter(names = {"-m", "--mqtt"}, required = true, description = "MQTT server hostname")
    public String mqtt_arg;
}
