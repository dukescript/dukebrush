package com.dukescript.dukebrush;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.oralb.sdk.OBTSDK;
import com.oralb.sdk.OBTSdkAuthorizationListener;

public class AndroidMain extends Activity {

    public AndroidMain() {
    }

    public static void main(String... args) throws Exception {
        DataModel.onPageLoad();
//        DataModel.ui.setRunning(false);
//        OBTSDK.startScanning();
    }

public void doStuffWithToothBrush() {
    try {
        OBTSDK.initialize(this);
        System.out.println("####################OBTSDK autorized");

    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    }
    OBTSDK.authorizeSdk(new OBTSdkAuthorizationListener() {
        @Override
        public void onSdkAuthorizationSuccess() {
            System.out.println("####################OBTSDK autorized");
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
