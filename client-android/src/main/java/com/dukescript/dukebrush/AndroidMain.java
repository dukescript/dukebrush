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
        System.out.println("####################main");

    }

    public void doStuffWithToothBrush() {
        System.out.println("####################doStuffWithToothBrush");
        try {
            OBTSDK.initialize(this);
            System.out.println("####################OBTSDK initialized");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        OBTSDK.authorizeSdk(new OBTSdkAuthorizationListener() {
            @Override
            public void onSdkAuthorizationSuccess() {
                System.out.println("####################OBTSDK authorized");
            }

            @Override
            public void onSdkAuthorizationFailed(int i) {
                System.out.println("####################onSdkAuthorizationFailed " + i);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("####################onCreate");

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
