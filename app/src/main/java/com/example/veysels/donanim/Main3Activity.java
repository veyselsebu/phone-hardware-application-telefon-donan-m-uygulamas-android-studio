package com.example.veysels.donanim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

public class Main3Activity extends AppCompatActivity {
    CalendarView takvim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        takvim=(CalendarView)findViewById(R.id.takvim1);
        Date simdikizaman=new Date();
        long abc=simdikizaman.getTime();
        takvim.setDate(abc);

    }
}
