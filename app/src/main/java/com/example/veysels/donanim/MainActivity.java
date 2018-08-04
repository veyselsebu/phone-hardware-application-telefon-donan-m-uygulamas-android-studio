package com.example.veysels.donanim;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    WifiManager wifi;
    BluetoothAdapter mBluetoothAdapter;
    Switch wifiS,bt,fener;
    TextView pil;
    TextView ekran1;
    AudioManager audioManager;
    SeekBar sesSeviyesi,parlaklık;
    Context context;
    int parlaklikSeviyesi;
    Button galeriGit,ayar,takvim;
    public static Camera cam = null;

private BroadcastReceiver pilYuzde =new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        int yuzde= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
    pil.setText("%"+yuzde);
    }
};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ayar=(Button)findViewById(R.id.ayar);
        galeriGit=(Button)findViewById(R.id.galeri);
        wifiS=(Switch)findViewById(R.id.wifi);
        bt=(Switch)findViewById(R.id.bt);
        fener=(Switch)findViewById(R.id.fener);
        pil=(TextView)findViewById(R.id.batarya);
        ekran1=(TextView)findViewById(R.id.tarih);
        parlaklık=(SeekBar)findViewById(R.id.parlak);
        sesSeviyesi=(SeekBar)findViewById(R.id.ses);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sesSeviyesi.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        takvim=(Button)findViewById(R.id.takvim);
        this.registerReceiver(this.pilYuzde,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Date dt=new Date();
        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        context=getApplicationContext();
        parlaklikSeviyesi= Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);
        parlaklık.setProgress(parlaklikSeviyesi);
        sesSeviyesi.setProgress(audioManager.getStreamVolume(1));

        parlaklık.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

                float yuzde = (float)( i /2.57);
                float c=yuzde+1;
                float LightValue = (float)yuzde/100;

                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.screenBrightness = LightValue;
                getWindow().setAttributes(layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
     sesSeviyesi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
});
        ayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent1 = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                startActivity(intent1);
            }
        });
        takvim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this,Main3Activity.class);

                startActivity(a);
            }
        });
        galeriGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent a = new Intent(MainActivity.this,Main2Activity.class);

                startActivity(a);
            }
        });
        fener.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    flashLightOn();
                else
                    flashLightOff();
            }
        });
        bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if (mBluetoothAdapter == null) {

                    }else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, 1);
                        }
                    }
                }else {
                    if (mBluetoothAdapter == null) {

                    }else {
                        if (!mBluetoothAdapter.isEnabled()) {

                        }else{
                            mBluetoothAdapter.disable();
                        }
                    }
                }
                }
        });

        wifiS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    if(wifi.getWifiState()== WifiManager.WIFI_STATE_DISABLED){
                        wifi.setWifiEnabled(true);
                    }else if(wifi.getWifiState()==WifiManager.WIFI_STATE_ENABLING){
                        Toast.makeText(MainActivity.this, "Açık", Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    if(wifi.getWifiState()== WifiManager.WIFI_STATE_ENABLED){
                        wifi.setWifiEnabled(false);
                    }else if(wifi.getWifiState()==WifiManager.WIFI_STATE_DISABLING){
                        Toast.makeText(MainActivity.this, "Zaten Kapalı", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


            ekran1.setText("cpu sıcaklığı :" + getCpuTemp());


    }
    public float getCpuTemp() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = reader.readLine();
            float temp = Float.parseFloat(line) / 1000.0f;

            return temp;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }


    public void flashLightOn() {

        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOn()",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void flashLightOff() {
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOff",
                    Toast.LENGTH_SHORT).show();
        }
    }




}
