package com.curve.nandhakishore.spidertwo;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends Activity {

    SensorManager sManager;
    Sensor proxySensor;
    SensorEventListener proxyListener;
    MediaPlayer mediaPlayer;
    TextView displayTime;
    int second, millisecond;
    long startTime, elapsedTime;
    Handler handler;

    public class cTimer extends Thread {
        @Override
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            if(elapsedTime < 5001) {
                calcTime(5000 - elapsedTime);
                Message running = new Message();
                running.what = 0;
                handler.sendMessage(running);
            }
            else{
                Message finished = new Message();
                finished.what = 1;
                handler.sendMessage(finished);
            }
            super.run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTime = (TextView) findViewById(R.id.timer);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxySensor = sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mediaPlayer = MediaPlayer.create(this, R.raw.coin);
        mediaPlayer.setLooping(true);
        final cTimer CT = new cTimer();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0){
                    displayTime.setText(String.valueOf(second + 1));
                    CT.start();
                }
                else if(msg.what == 1){
                    displayTime.setText("0");
                    mediaPlayer.start();
                }
                super.handleMessage(msg);
            }
        };

        proxyListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[0] < proxySensor.getMaximumRange()) {
                    getWindow().getDecorView().setBackgroundColor(Color.rgb(211, 47, 47));
                    startTime=SystemClock.uptimeMillis();
                    elapsedTime=0;
                    displayTime.setText("5");
                    CT.start();
                }
                else {
                    getWindow().getDecorView().setBackgroundColor(Color.rgb(56, 142, 60));
                    displayTime.setText("5");
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        try{
                            mediaPlayer.prepare();
                        }catch (Exception err){
                            err.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sManager.registerListener(proxyListener, proxySensor, 500 * 1000);
    }

    @Override
    protected void onResume() {
        sManager.registerListener(proxyListener, proxySensor, 500 * 1000);
        super.onResume();
    }

    private void calcTime(long l){
        second = (int) l / 1000;
    }

    @Override
    protected void onPause() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try{
                mediaPlayer.prepare();
            }catch (Exception err){
                err.printStackTrace();
            }
        }
        sManager.unregisterListener(proxyListener);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
