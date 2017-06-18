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
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    SensorManager sManager;
    Sensor proxySensor;
    SensorEventListener proxyListener;
    MediaPlayer mediaPlayer;
    CountDownTimer timer;
    Boolean isRunning = false;
    TextView displayTime;
    int second, millisecond;
    long startTime, elapsedTime;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTime = (TextView) findViewById(R.id.timer);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxySensor = sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mediaPlayer = MediaPlayer.create(this, R.raw.coin);
        mediaPlayer.setLooping(true);

        handler = new Handler();

       /* timer = new CountDownTimer(5000, 10) {
            @Override
            public void onTick(long l) {
                calcTime(l);
                displayTime.setText(String.format("%02d", second) + ":" + String.format("%02d", millisecond));
                isRunning = true;
            }

            @Override
            public void onFinish() {
                mediaPlayer.start();
                displayTime.setText("00:00");
                isRunning = false;
            }

        }; */

       proxyListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[0] < proxySensor.getMaximumRange()) {
                    getWindow().getDecorView().setBackgroundColor(Color.rgb(211, 47, 47));
                    displayTime.setText("5");
                    startTime = SystemClock.uptimeMillis();
                    handler.post(cTimer);
                }
                else {
                    getWindow().getDecorView().setBackgroundColor(Color.rgb(56, 142, 60));
                    handler.removeCallbacks(cTimer);
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

    public Runnable cTimer = new Runnable() {
        @Override
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            if(elapsedTime < 5001) {
                calcTime(5000 - elapsedTime);
                Log.e("Time remaining", String.format("%02d", second) + ":" + String.format("%01d", millisecond));
                displayTime.setText(String.valueOf(second + 1));
                handler.post(this);
            }
            else{
                displayTime.setText("0");
                mediaPlayer.start();
            }
        }
    };

    @Override
    protected void onResume() {
        sManager.registerListener(proxyListener, proxySensor, 500 * 1000);
        super.onResume();
    }

    private void calcTime(long l){
        second = (int) l / 1000;
        millisecond = (int) (l % 1000) / 100 ;
    }

    @Override
    protected void onPause() {
        if (isRunning) {
            timer.cancel();
        }
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
