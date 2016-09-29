package com.dukescript.dukebrush;

import java.util.EventListener;


/**
 * Created by hansolo on 23.01.15.
 */
public interface MqttEventListener extends EventListener {
    void onMqttEvent(MqttEvent event);
}
