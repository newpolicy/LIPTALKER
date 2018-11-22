package com.liptalker.home.liptalker;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.liptalker.home.liptalker.fragment.PeopleFragment;

public class MainActivity extends AppCompatActivity {
    Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,
                new PeopleFragment()).commit();

        /*mHandler = new Handler(); Thread t = new Thread(new Runnable(){ @Override public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
            }
        });
        t.start();*/

    }
}
