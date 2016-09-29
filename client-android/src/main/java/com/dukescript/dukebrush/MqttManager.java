package com.dukescript.dukebrush;

import android.util.Log;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hansolo on 23.01.15.
 */
public class MqttManager implements MqttCallback {
    private static final String             TAG                  = "MqttManager";
    public static final  String             BROKER_URL           = "tcp://hansolo.info";
    public static final  int                PORT                 = 1883;
    public static final  int                QOS_0                = 0;
    public static final  int                QOS_1                = 1;
    public static final  int                QOS_2                = 2;
    public static final  boolean            RETAINED             = true;
    public static final  boolean            NOT_RETAINED         = false;

    // Client
    public static final  String             CLIENT_ID            = "Brush1";

    // Topics
    public static final  String             PRESENCE_TOPIC       = "presence";
    public static final  String             BRUSH_TOPIC          = "brush";

    // Credentials
    private static final String             USERNAME             = "hansolo";
    private static final String             PASSWORD_MD5         = "alliance";

    private              MqttClient         client;
    private              MqttConnectOptions clientConnectOptions;
    private              String             clientId;

    // Reconnection
    private              Thread             reconnectionThread;
    private              int                randomBase           = new Random().nextInt(11) + 5; // between 5 and 15 seconds
    private              boolean            connected;

    private              CopyOnWriteArrayList<MqttEventListener> listenerList = new CopyOnWriteArrayList<>();


    // ******************* Constructors ***************************************
    public MqttManager() {
        this(CLIENT_ID);
    }
    public MqttManager(final String MQTT_CLIENT_ID) {
        clientId = MQTT_CLIENT_ID;
        init();
    }


    // ******************* Initialization *************************************
    private void init() {
        clientConnectOptions = new MqttConnectOptions();
        clientConnectOptions.setCleanSession(true);
        clientConnectOptions.setKeepAliveInterval(1200);
        //clientConnectOptions.setConnectionTimeout(5000);
        clientConnectOptions.setUserName(USERNAME);
        clientConnectOptions.setPassword(PASSWORD_MD5.toCharArray());
        clientConnectOptions.setWill(PRESENCE_TOPIC + "/" + clientId, "0".getBytes(), QOS_2, RETAINED);

        connected = false;

        //connectToBroker();
    }

    public void reInit(final String MQTT_CLIENT_ID) {
        if (connected) {
            publish(QOS_2, RETAINED, PRESENCE_TOPIC + "/" + clientId, "");
            publish(QOS_0, RETAINED, MqttManager.BRUSH_TOPIC + "/" + clientId, "");
            disconnect(2000);
        }
        clientId = MQTT_CLIENT_ID;
        init();
        connectToBroker();
    }


    // ******************** Methods *******************************************
    public void connectToBroker() {
        try {
            client = new MqttClient(BROKER_URL + ":" + PORT, clientId, new MemoryPersistence());
            client.setCallback(this);
            client.connect(clientConnectOptions);

            connected = true;

            subscribeToTopics();

            Log.i(TAG, "MQTT connection established");
        } catch (MqttException exception) {
            connected = false;
            reconnect();
        }
    }

    private void subscribeToTopics() {
        try {
            //Log.i(TAG, "Successfully subscribed to topics");

            // Publish presence message
            publishOnlineMessage(clientId);
        } catch (Exception exception) {
            Log.i(TAG, "Error subscribing to messages: " + exception.toString());
        }
    }

    public void publish(final int QOS, final boolean RETAINED, final String TOPIC, final String MESSAGE) {
        publish(QOS, RETAINED, TOPIC, MESSAGE.getBytes());
    }
    public void publish(final int QOS, final boolean RETAINED, final String TOPIC, final byte[] PAYLOAD) {
        if (null == client) connectToBroker();
        if (null != client && client.isConnected()) {
            try {
                MqttTopic topic = client.getTopic(TOPIC);
                MqttMessage message = new MqttMessage(PAYLOAD);
                message.setQos(QOS);
                message.setRetained(RETAINED);
                MqttDeliveryToken token = topic.publish(message);
                //token.waitForCompletion(100);
                Thread.sleep(10);
            } catch (MqttException exception) {} catch (InterruptedException exception) {}
        } else {
            connected = false;
        }
    }

    public void subscribe(final String TOPIC, final int QOS) {
        if (null == client) connectToBroker();
        if (null != client && client.isConnected()) {
            try {
                client.subscribe(TOPIC, QOS);
            } catch (MqttException exception) {
                Log.i(TAG, "Error subscribing to topic " + TOPIC + ": " + exception.toString());
            }
        }
    }

    public void unsubscribeTopics() {
    }

    public void publishOnlineMessage(final String CLIENT_ID) {
        publish(QOS_2, RETAINED, PRESENCE_TOPIC + "/" + CLIENT_ID, "1");
    }
    public void publishOfflineMessage(final String CLIENT_ID) {
        publish(QOS_2, RETAINED, PRESENCE_TOPIC + "/" + CLIENT_ID, "0");
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect(final long TIMEOUT) {
        if (null == client || !client.isConnected()) return;
        try {
            client.disconnect(TIMEOUT);
        } catch (MqttException exception) {
            //LogManager.INSTANCE.logWarning(MqttManager.class, "Error disconnecting MQTT client");
        }
    }


    // ******************** Reconnection **************************************
    private boolean isReconnectionAllowed() {
        return !client.isConnected();
    }

    synchronized protected void reconnect() {
        if (client.isConnected() && reconnectionThread != null && reconnectionThread.isAlive()) return;

        reconnectionThread = new Thread() {

            private int attempts = 0;

            /**
             * Returns the number of seconds until the next reconnection attempt.
             * @return the number of seconds until the next reconnection attempt.
             */
            private int timeDelay() {
                attempts++;
                if (attempts > 13) {
                    return randomBase * 6 * 5; // between 2.5 and 7.5 minutes (~5 minutes)
                }
                if (attempts > 7) {
                    return randomBase * 6;     // between 30 and 90 seconds (~1 minutes)
                }
                return randomBase;             // 10 seconds
            }

            /**
             * The process will try the reconnection until the connection
             * succeed or the user cancel it
             */
            public void run() {
                while (MqttManager.this.isReconnectionAllowed()) {
                    int remainingSeconds = timeDelay();

                    while (MqttManager.this.isReconnectionAllowed() && remainingSeconds > 0) {
                        try {
                            Thread.sleep(1000);
                            remainingSeconds--;
                        } catch (InterruptedException exception) {
                            connected = false;
                        }
                    }

                    // Makes a reconnection attempt
                    try {
                        if (MqttManager.this.isReconnectionAllowed()) {
                            client.connect(clientConnectOptions);
                            if (client.isConnected()) {
                                connected = true;
                                subscribeToTopics();
                                //LogManager.INSTANCE.logInfo(MqttManager.class, "MQTT client successfully reconnected");
                            }
                        }
                    } catch (MqttException exception) {
                        // Fires the failed reconnection notification
                        connected = false;
                    }
                }
            }
        };
        reconnectionThread.setName("MQTT Reconnection Manager");
        reconnectionThread.setDaemon(false);
        reconnectionThread.start();
    }


    // ******************** Event handling ************************************
    @Override public void connectionLost(final Throwable CAUSE) {
        Log.i(TAG, "Mqtt connection lost, trying to reconnect");
        reconnect();
    }
    @Override public void messageArrived(final String TOPIC, final MqttMessage MQTT_MESSAGE) {
        fireMqttEvent(new MqttEvent(this, TOPIC, MQTT_MESSAGE));
    }
    @Override public void deliveryComplete(final IMqttDeliveryToken TOKEN) {}


    public final void setOnMessageReceived(final MqttEventListener LISTENER) { listenerList.add(LISTENER); }
    public final void addMqttEventListener(final MqttEventListener LISTENER) { listenerList.add(LISTENER); }
    public final void removeMqttEventListener(final MqttEventListener LISTENER) { listenerList.remove(LISTENER); }

    public void fireMqttEvent(final MqttEvent EVENT) {
        for (MqttEventListener listener : listenerList) { listener.onMqttEvent(EVENT); }
    }
}
