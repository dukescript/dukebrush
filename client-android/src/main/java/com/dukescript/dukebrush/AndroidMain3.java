package com.dukescript.dukebrush;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import com.oralb.sdk.OBTBrush;

import com.oralb.sdk.OBTSDK;
import com.oralb.sdk.OBTBrushListener;
import com.oralb.sdk.OBTSdkAuthorizationListener;
import java.util.List;

public class AndroidMain3 extends Activity {//implements MqttEventListener {

    private PowerManager powerManager;
//    private MqttManager mqttManager;

    public AndroidMain3() {

    }

    public static void main(String... args) throws Exception {
        DataModel.onPageLoad();
        DataModel.ui.setRunning(false);

    }

    public void doStuffWithToothBrush() {
//        initPowerManager();
//        initMqttManager();
        try {
            System.out.println("1#######################################");
            OBTSDK.initialize(this);
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("2#######################################");
            e.printStackTrace();
        }
        final OBTBrushListener listener = new OBTBrushListener() {
            long handleId = -1;

            @Override
            public void onNearbyBrushesFoundOrUpdated(List<OBTBrush> list) {
            System.out.println("3#######################################");
                if (list.size() > 0) {

                    DataModel.ui.setRunning(true);
                    final OBTBrush brush = list.get(0);

                    if (!OBTSDK.isConnected()) {
                        OBTSDK.connectToothbrush(brush, true);
                    }
                    handleId = brush.getHandleId();
                    System.out.println("###time: " + brush.getBrushingTime());
                    System.out.println("###sector: " + brush.getCurrentSector());
                    System.out.println("###id: " + brush.getLocalHandleId());
                    System.out.println("###level: " + brush.getBatteryLevel());
                    System.out.println("###mode: " + brush.getCurrentBrushMode());
                    System.out.println("###state: " + brush.getCurrentBrushState());
                    DataModel.ui.setBattery(brush.getBatteryLevel());
                    DataModel.ui.setMode(brush.getCurrentBrushMode());
                    DataModel.ui.setState(brush.getCurrentBrushState());
                    DataModel.ui.setSector(brush.getCurrentSector());
                    DataModel.ui.setHighPressure(brush.isHighPressure());
                    sendMessage();
                } else {
//                    DataModel.ui.setRunning(false);
//                    publishBrushData(handleId, false, -1);
                }
            }

            @Override
            public void onBluetoothError() {
                System.out.println("onBluetoothError");
            System.out.println("4#######################################");
            }

            @Override
            public void onBrushDisconnected() {
            System.out.println("5#######################################");
                DataModel.ui.setRunning(false);
                sendMessage();
            }

            @Override
            public void onBrushConnected() {
            System.out.println("6#######################################");
                DataModel.ui.setRunning(true);
                sendMessage();

            }

            @Override
            public void onBrushConnecting() {
            System.out.println("7#######################################");

            }

            @Override
            public void onBrushingTimeChanged(long l) {
            System.out.println("8#######################################");
                DataModel.ui.setTime(l);
                sendMessage();
            }

            @Override
            public void onBrushingModeChanged(int i) {
            System.out.println("9#######################################");
                DataModel.ui.setMode(i);
            }

            @Override
            public void onBrushStateChanged(int i) {
            System.out.println("10#######################################");
                DataModel.ui.setState(i);
            }

            @Override
            public void onRSSIChanged(int i) {
            }

            @Override
            public void onBatteryLevelChanged(float f) {
                DataModel.ui.setBattery(f);
                sendMessage();
            }

            @Override
            public void onSectorChanged(int i) {
                DataModel.ui.setSector(i);
                sendMessage();
            }

            @Override
            public void onHighPressureChanged(boolean bln) {
                DataModel.ui.setHighPressure(bln);
                sendMessage();
            }

            private void sendMessage() {
//                publishBrushData(handleId,
//                        DataModel.ui.getTime(),
//                        DataModel.ui.isRunning(),
//                        DataModel.ui.isHighPressure(),
//                        DataModel.ui.getSector(),
//                        DataModel.ui.getBattery());
            }
        };

        OBTSDK.authorizeSdk(new OBTSdkAuthorizationListener() {
            @Override
            public void onSdkAuthorizationSuccess() {
            System.out.println("11#######################################");
                System.out.println("####################OBTSDK autorized");
                OBTSDK.setOBTBrushListener(listener);
                OBTSDK.startScanning();
            }

            @Override
            public void onSdkAuthorizationFailed(int i) {
            System.out.println("12#######################################");
                System.out.println("####################onSdkAuthorizationFailed");

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("0#######################################");
        doStuffWithToothBrush();
        try {
            // delegate to original activity
            startActivity(new Intent(getApplicationContext(), Class.forName("com.dukescript.presenters.Android")));
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
        finish();
    }

    // ******************** Initialization ************************************
//    private void initPowerManager() {
//        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//    }
//    private void initMqttManager() {
//        mqttManager = new MqttManager();
//        mqttManager.connectToBroker();
//        mqttManager.setOnMessageReceived(this);
//    }
    // ******************** MQTT related **************************************
//    @Override
//    public void onMqttEvent(final MqttEvent EVENT) {
//        final String TOPIC = EVENT.TOPIC;
//        final String MQTT_MESSAGE = EVENT.MESSAGE.toString();
//        if (MQTT_MESSAGE.isEmpty()) {
//            return;
//        }
}

// ******************** Methods *******************************************
//    public void publishBrushData(final long ID,
//            final long TIME,
//            final boolean ONLINE,
//            final boolean HIGH_PRESSURE,
//            final int SECTOR,
//            final float BATTERY_LEVEL) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // Info from Brush as JSON
//                StringBuilder brushInfo = new StringBuilder();
//                brushInfo.append("{\n")
//                        .append("  \"id\":\"").append(ID).append("\",\n")
//                        .append("  \"online\":").append(ONLINE).append(",\n")
//                        .append("  \"time\":\"").append(TIME).append("\",\n")
//                        .append("  \"sector\":\"").append(SECTOR).append("\",\n")
//                        .append("  \"highPressure\":\"").append(HIGH_PRESSURE).append("\",\n")
//                        .append("  \"battery\":").append(BATTERY_LEVEL).append("\n")
//                        .append("}");
//
//                mqttManager.publish(MqttManager.QOS_0, MqttManager.NOT_RETAINED, MqttManager.BRUSH_TOPIC + "/" + MqttManager.CLIENT_ID, brushInfo.toString());
//
//            }
//        }).start();

