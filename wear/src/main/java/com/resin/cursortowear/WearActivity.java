package com.resin.cursortowear;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import jp.ogwork.gesturetransformableview.view.GestureTransformableImageView;

public class WearActivity extends WearableActivity
        implements MessageApi.MessageListener {

    String TAG = "Watch";
    GestureTransformableImageView mGestureTransformableImageView;
    GestureDetector mGestureDetector;

    GoogleApiClient mGoogleApiClient;
    String MESSAGE = "/message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        setAmbientEnabled();

        mGestureTransformableImageView = (GestureTransformableImageView) findViewById(R.id.gestureIV);
        mGestureTransformableImageView.setLimitScaleMax(400);   // default: 270

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                Log.d(TAG, "long tap");
                finish();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(TAG, "double tap");
                setContentView(R.layout.activity_wear);
                return super.onDoubleTap(e);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "Google Api Client connected");

                        Wearable.MessageApi.addListener(mGoogleApiClient, WearActivity.this);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                }).build();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {  // move,210.0,220.0
        if (MESSAGE.equals(messageEvent.getPath())) {
            String msg = new String(messageEvent.getData());

//            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

            String[] data = msg.split(",");

            Log.d(TAG, data[0] + " " + data[1] + " " + data[2]);

            float x = Float.parseFloat(data[1]), y = Float.parseFloat(data[2]);
            long time = SystemClock.uptimeMillis();
            int dur = 1; // duration

            switch (data[0]) {
                case "down":
//                    Log.d(TAG, "down");
                    dispatchTouchEvent(
                            MotionEvent.obtain(time, time + dur, MotionEvent.ACTION_DOWN, x, y, 0));
                    break;

                case  "move":
//                    Log.d(TAG, "move");
                    dispatchTouchEvent(
                            MotionEvent.obtain(time, time + dur, MotionEvent.ACTION_MOVE, x, y, 0));
                    break;

                case  "up":
//                    Log.d(TAG, "up");
                    dispatchTouchEvent(
                            MotionEvent.obtain(time, time + dur, MotionEvent.ACTION_UP,  x, y, 0));
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event) || super.dispatchTouchEvent(event);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }
}
