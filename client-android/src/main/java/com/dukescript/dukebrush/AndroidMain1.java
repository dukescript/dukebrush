package com.dukescript.dukebrush;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.oralb.sdk.OBTBrush;

import com.oralb.sdk.OBTSDK;
import com.oralb.sdk.OBTBrushListener;
import com.oralb.sdk.OBTSdkAuthorizationListener;
import java.util.List;

public class AndroidMain1 extends Activity {

    public AndroidMain1() {
    }

    public static void main(String... args) throws Exception {
        DataModel.onPageLoad();
    }

    public void doStuffWithToothBrush() {
        try {
            OBTSDK.initialize(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final OBTBrushListener listener = new OBTBrushListener() {
            @Override
            public void onNearbyBrushesFoundOrUpdated(List<OBTBrush> list) {
                if (list.size() > 0) {
                    DataModel.ui.setRunning(true);
                    final OBTBrush brush = list.get(0);
                    if (!OBTSDK.isConnected()) {
                        OBTSDK.connectToothbrush(brush, true);
                    }
                    System.out.println("###time: " + brush.getBrushingTime());
                    System.out.println("###sector: " + brush.getCurrentSector());
                    System.out.println("###id: " + brush.getLocalHandleId());
                    System.out.println("###level: " + brush.getBatteryLevel());
                    System.out.println("###mode: " + brush.getCurrentBrushMode());
                    System.out.println("###state: " + brush.getCurrentBrushState());
                }
            }
            // lots of interface methods...

            @Override
            public void onBluetoothError() {
                System.out.println(
                        "############## Bluetooth error");

            }

            @Override
            public void onBrushDisconnected() {
                 System.out.println(
                        "############## onBrushDisconnected");
            }

            @Override
            public void onBrushConnected() {
                 System.out.println(
                        "############## onBrushConnected");
            }

            @Override
            public void onBrushConnecting() {
                System.out.println(
                        "############## onBrushConnecting");
            }

            @Override
            public void onBrushingTimeChanged(long l) {
            }

            @Override
            public void onBrushingModeChanged(int i) {
            }

            @Override
            public void onBrushStateChanged(int i) {
            }

            @Override
            public void onRSSIChanged(int i) {
            }

            @Override
            public void onBatteryLevelChanged(float f) {
            }

            @Override
            public void onSectorChanged(int i) {
            }

            @Override
            public void onHighPressureChanged(boolean bln) {
            }

        };

        OBTSDK.authorizeSdk(new OBTSdkAuthorizationListener() {
            @Override
            public void onSdkAuthorizationSuccess() {
                System.out.println("################ Authorization succeeded");

                OBTSDK.setOBTBrushListener(listener);
                OBTSDK.startScanning();
            }

            @Override
            public void onSdkAuthorizationFailed(int i) {
                System.out.println("################ Authorization failed "+i);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doStuffWithToothBrush();
        try {
            // delegate to original activity
            startActivity(new Intent(getApplicationContext(), Class.forName("com.dukescript.presenters.Android")));
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
        finish();
    }

}
