package org.athenian;

import org.athenian.args.BrokerArgs;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

public class RobotSimulation {

    /*
     In this simulation, the the values are published faster than the robot can act upon them.
     So we act only on the most recent values that we read and let soe fall on the floor
     The assignment of the values and the timestamp take place in a synchronized block to ensure
     they are read/written together. If we were not dealing with timestamps, no synchronization
     would be necessary.
    */

    static final int SUBSCRIBE_PAUSE = 100;
    static final int WORK_PAUSE = 1000;
    static final String CAMERA_TOPIC = "camera/loc";
    static final String LIDAR_TOPIC = "lidar/dist";
    static final Random random = new Random();

    public static void main(final String[] argv) throws InterruptedException, MqttException {

        final BrokerArgs cliArgs = new BrokerArgs();
        cliArgs.parseArgs(RobotSimulation.class.getName(), argv);

        final MqttClient client = Utils.createMqttClient(Utils.getMqttHostname(cliArgs.mqtt_arg),
                                                         Utils.getMqttPort(cliArgs.mqtt_arg),
                                                         new BaseMqttCallback());
        if (client == null)
            return;

        final AtomicInteger current_loc = new AtomicInteger();
        final AtomicLong loc_ts = new AtomicLong();
        final AtomicInteger current_dist = new AtomicInteger();
        final AtomicLong dist_ts = new AtomicLong();

        // The subscribe callbacks run into their own thread
        try {
            client.subscribe(CAMERA_TOPIC,
                             new IMqttMessageListener() {
                                 @Override
                                 public void messageArrived(String topic, MqttMessage msg) throws Exception {
                                     final String val = new String(msg.getPayload());
                                     System.out.println(format("%s : %s", topic, val));
                                     synchronized (current_loc) {
                                         current_loc.set(Integer.parseInt(val));
                                         loc_ts.set(System.currentTimeMillis());
                                     }
                                 }
                             });

            client.subscribe(LIDAR_TOPIC,
                             new IMqttMessageListener() {
                                 @Override
                                 public void messageArrived(String topic, MqttMessage msg) throws Exception {
                                     final String val = new String(msg.getPayload());
                                     System.out.println(format("%s : %s", topic, val));
                                     synchronized (current_dist) {
                                         current_dist.set(Integer.parseInt(val));
                                         dist_ts.set(System.currentTimeMillis());
                                     }
                                 }
                             });
        }
        catch (MqttException e) {
            System.out.println(format("Unable to subscribe [%s]", e.getMessage()));
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(4);
        publishData(client, executorService);

        /*
        Non-lambda approach to starting a Runnable in a Thread:
        executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        // Action of thread
                    }
                });

        Lambda approach to starting a Runnable in a Thread:
        executorService.submit(() -> {
            // Action of thread
        });
        */


        // Run the robot actions in separate threads, one for location and one for distance
        executorService.submit(() -> {
            long last_read = 0;
            while (true) {
                final int loc;
                synchronized (current_loc) {
                    // Do not act upon stale data
                    if (loc_ts.get() <= last_read)
                        continue;
                    last_read = loc_ts.get();
                    loc = current_loc.get();
                }
                // This takes place outside of the sync block
                System.out.println(String.format("Doing something with x location %d", loc));
                sleep(random.nextInt(WORK_PAUSE));
            }
        });

        executorService.submit(() -> {
            long last_read = 0;
            while (true) {
                final int dist;
                synchronized (current_dist) {
                    // Do not act upon stale data
                    if (dist_ts.get() <= last_read)
                        continue;
                    last_read = dist_ts.get();
                    dist = current_dist.get();
                }
                // This takes place outside of the sync block
                System.out.println(String.format("Doing something with distance %d", dist));
                sleep(random.nextInt(WORK_PAUSE));
            }
        });

        sleep(Integer.MAX_VALUE);
    }

    public static MqttMessage newMqttMessage(final Object val) {
        final byte[] b = ("" + val).getBytes();
        return new MqttMessage(b);
    }

    public static void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void publishData(final MqttClient client, final ExecutorService executorService) {
        // Simulate data being published in different threads
        executorService.submit(() -> {
            try {
                while (true) {
                    for (int i = 0; i < 128; i++) {
                        client.publish(CAMERA_TOPIC, newMqttMessage(i));
                        sleep(random.nextInt(SUBSCRIBE_PAUSE));
                    }
                    for (int i = 128; i > 0; i--) {
                        client.publish(CAMERA_TOPIC, newMqttMessage(i));
                        sleep(random.nextInt(SUBSCRIBE_PAUSE));
                    }
                }
            }
            catch (MqttException e) {
                System.out.println(format("Unable to publish data to %s [%s]", CAMERA_TOPIC, e.getMessage()));
            }
        });

        executorService.submit(() -> {
            try {
                while (true) {
                    for (int i = 0; i < 15; i++) {
                        client.publish(LIDAR_TOPIC, newMqttMessage(i));
                        sleep(random.nextInt(SUBSCRIBE_PAUSE));
                    }
                    for (int i = 15; i > 0; i--) {
                        client.publish(LIDAR_TOPIC, newMqttMessage(i));
                        sleep(random.nextInt(SUBSCRIBE_PAUSE));
                    }
                }
            }
            catch (MqttException e) {
                System.out.println(format("Unable to publish data to %s [%s]", LIDAR_TOPIC, e.getMessage()));
            }
        });

    }
}
