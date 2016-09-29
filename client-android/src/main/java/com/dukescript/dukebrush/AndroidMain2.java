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

public class AndroidMain2 extends Activity {

    public AndroidMain2() {}

public static void main(String... args) throws Exception {
    DataModel.onPageLoad();
    DataModel.ui.setRunning(false);
    OBTSDK.startScanning();
}

    public void doStuffWithToothBrush() {
        try {
            OBTSDK.initialize(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final OBTBrushListener listener = new OBTBrushListener() {
            long handleId = -1;

            @Override
            public void onNearbyBrushesFoundOrUpdated(List<OBTBrush> list) {
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
                } else {
//                    DataModel.ui.setRunning(false);
//                    publishBrushData(handleId, false, -1);
                }
            }

            @Override
            public void onBluetoothError() {
                System.out.println("onBluetoothError");
            }

@Override
public void onBrushDisconnected() {
    DataModel.ui.setRunning(false);
}

@Override
public void onBrushConnected() {
    DataModel.ui.setRunning(true);
}


@Override
public void onBrushingTimeChanged(long l) {
    DataModel.ui.setTime(l);
}

            @Override
            public void onBrushConnecting() {

            }
            @Override
            public void onBrushingModeChanged(int i) {
                DataModel.ui.setMode(i);
            }

            @Override
            public void onBrushStateChanged(int i) {
                DataModel.ui.setState(i);
            }

            @Override
            public void onRSSIChanged(int i) {
            }

            @Override
            public void onBatteryLevelChanged(float f) {
                DataModel.ui.setBattery(f);
            }

            @Override
            public void onSectorChanged(int i) {
                DataModel.ui.setSector(i);
            }

            @Override
            public void onHighPressureChanged(boolean bln) {
                DataModel.ui.setHighPressure(bln);
            }

        };

        OBTSDK.authorizeSdk(new OBTSdkAuthorizationListener() {
            @Override
            public void onSdkAuthorizationSuccess() {
                System.out.println("####################OBTSDK autorized");
                OBTSDK.setOBTBrushListener(listener);
            }

            @Override
            public void onSdkAuthorizationFailed(int i) {
                System.out.println("####################onSdkAuthorizationFailed");
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
