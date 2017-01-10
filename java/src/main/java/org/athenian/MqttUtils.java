package org.athenian;

public class MqttUtils {

    public static String getMqttHostname(String val) {
        return val.contains(":") ? val.substring(0, val.indexOf(":")) : val;
    }

    public static int getMqttPort(String val) {
        return val.contains(":") ? Integer.parseInt(val.substring(val.indexOf(":") + 1, val.length())) : 1883;
    }
}
