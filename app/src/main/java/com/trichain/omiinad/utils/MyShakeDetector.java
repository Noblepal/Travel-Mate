package com.trichain.omiinad.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

import static android.content.Context.MODE_PRIVATE;

public class MyShakeDetector extends safety.com.br.android_shake_detector.core.ShakeDetector {

    private Context context;
    private static final String TAG = "MyShakeDetector";
    private ShakeDetector mDetector;
    private static MyShakeDetector myShakeDetector;

    public MyShakeDetector(Context context) {
        this.context = context;
    }

    public static synchronized MyShakeDetector getInstance(Context context) {
        if (myShakeDetector == null) {
            myShakeDetector = new MyShakeDetector(context);
        }
        return myShakeDetector;
    }

    public void instantiateShakeDetector() {
        ShakeOptions mOptions = new ShakeOptions()
                .background(true)
                .interval(1000)
                .shakeCount(2)
                .sensibility(2.0f);

        mDetector = new ShakeDetector(mOptions).start(context, () -> {
            Log.e("event", "onShake");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (context.getSharedPreferences("MyPref", MODE_PRIVATE).getBoolean("shake_me", true)) {
                context.startActivity(intent);
            }
        });
    }

    public void stopShake() {
        if (mDetector.isRunning())
            mDetector.stopShakeDetector(context);
    }

    public void destroy() {
        try {
            mDetector.destroy(context);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
    }
}
