package org.athenian;

import com.beust.jcommander.Parameter;

public class TopicCliArgs extends ServerCliArgs {
    @Parameter(names = {"-t", "--topic"}, required = true, description = "MQTT topic")
    public String mqtt_topic;

}
