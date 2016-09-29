package com.dukescript.dukebrush;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.EventObject;


/**
 * Created by hansolo on 23.01.15.
 */
public class MqttEvent extends EventObject {
    public final String      TOPIC;
    public final MqttMessage MESSAGE;


    public MqttEvent(Object source, final String MQTT_TOPIC, final MqttMessage MQTT_MESSAGE) {
        super(source);
        TOPIC   = MQTT_TOPIC;
        MESSAGE = MQTT_MESSAGE;
    }
}
