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
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    SensorManager sManager;
    Sensor proxySensor;
    SensorEventListener proxyListener;
    MediaPlayer mediaPlayer;
    CountDownTimer timer;
    TextView displayTime;
    int second, millisecond;
    Boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTime = (TextView) findViewById(R.id.timer);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxySensor = sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mediaPlayer = MediaPlayer.create(this, R.raw.coin);
        mediaPlayer.setLooping(true);

        timer = new CountDownTimer(5000, 10) {
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

        };

        proxyListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.e("PROXIMITY", String.valueOf(sensorEvent.values[0]));
                if(sensorEvent.values[0] < proxySensor.getMaximumRange()) {
                    getWindow().getDecorView().setBackgroundColor(0xC62828);
                    displayTime.setText("05:00");
                    timer.start();
                }
                else {
                    getWindow().getDecorView().setBackgroundColor(0x2E7D32);
                    Log.e("MPlayer", String.valueOf(mediaPlayer.isPlaying()));
                    if (isRunning) {
                        timer.cancel();
                    }
                    displayTime.setText("05:00");
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
        millisecond = (int) (l - (second*1000))/10;
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
