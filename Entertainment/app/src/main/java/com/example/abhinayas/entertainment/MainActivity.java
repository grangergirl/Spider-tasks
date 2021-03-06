package com.example.abhinayas.entertainment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
    public int currentimageindex=0;
    ImageView img;
    int period = 3000;
    private int[] IMAGES = {
            R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4, R.drawable.pic5, R.drawable.pic6, R.drawable.pic7
    };
    public Timer timer,timeKeeper;
    public Handler mHandler,timehandler,swipehandler;
    private Button play, stop;
    public MediaPlayer mediaPlayer;
    public int timeNow=0,min=0,sec=0;
    public TextView name;
    private Spinner spinner;
    private static String[] paths = {"", "A life full of love", "Cuppy cake", "Debussy","Here I am"};
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    public int startindex;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private SensorEventListener listen;
    public int val;
    public Button enable,disable,save;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Closing app", Toast.LENGTH_SHORT).show();
      //  mediaPlayer.stop();
       // mediaPlayer.reset();
        onStop();
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                img = (ImageView)findViewById(R.id.img);

                if (-deltaX > MIN_DISTANCE)
                {
                    Toast.makeText(this, "Next picture", Toast.LENGTH_SHORT).show ();

                    if(currentimageindex<IMAGES.length-1) {
                        currentimageindex++;
                        img.setImageResource(IMAGES[currentimageindex]);
                        Animation rotateimage = AnimationUtils.makeInAnimation(this, false);
                        img.startAnimation(rotateimage);
                    }
                    else
                    { currentimageindex=0;
                        img.setImageResource(IMAGES[currentimageindex]);
                        Animation rotateimage = AnimationUtils.makeInAnimation(this, false);
                        img.startAnimation(rotateimage);
                    }

                    //iv.setImageResource(R.drawable.near);
                }
                else if(deltaX>MIN_DISTANCE)
                {
                    // consider as something else - a screen tap for example
                    Toast.makeText(this, "Previous picture", Toast.LENGTH_SHORT).show ();
                    if(currentimageindex>0) {
                        currentimageindex--;
                        img.setImageResource(IMAGES[currentimageindex]);
                        Animation rotateimage = AnimationUtils.makeInAnimation(this, true);
                        img.startAnimation(rotateimage);
                    }
                    else
                    { currentimageindex=IMAGES.length-1;
                        img.setImageResource(IMAGES[currentimageindex]);
                        Animation rotateimage = AnimationUtils.makeInAnimation(this, true);
                        img.startAnimation(rotateimage);
                    }


                }
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        timehandler=new Handler();
        swipehandler=new Handler();


       // save=(Button)findViewById(R.id.bSave);
       // title=(TextView)findViewById(R.id.tvTitlePU);
       // count=(TextView)findViewById(R.id.tvCount);
        disable.setEnabled(false);
        //save.setEnabled(false);

        Button s=(Button)findViewById(R.id.start);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button s=(Button)findViewById(R.id.start);
                s.setEnabled(false);
                taskRepeat();
            }
        });
        play = (Button) findViewById(R.id.play);
        stop= (Button) findViewById(R.id.stop);
        Button e=(Button)findViewById(R.id.enable);
        Button d=(Button)findViewById(R.id.disable);
        d.setEnabled(false);
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button e=(Button)findViewById(R.id.enable);
                Button d=(Button)findViewById(R.id.disable);
                startCounter(v);
                d.setEnabled(true);
                e.setEnabled(false);

            }
        });
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button e=(Button)findViewById(R.id.enable);
                Button d=(Button)findViewById(R.id.disable);
                stopCounter(v);
                e.setEnabled(true);
                d.setEnabled(false);
                //stuff

            }
        });



        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {

                val=pos;
                if(val==0)
                    Toast.makeText(getApplicationContext(), "Choose a proper audio file", Toast.LENGTH_SHORT).show();


            }


            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
                playSong();
                play.setEnabled(false);
                stop.setEnabled(true);

            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mediaPlayer.stop();
                mediaPlayer.reset();
                onStop();
                stop.setEnabled(false);
                play.setEnabled(true);

            }
        });





    }
    @Override
    protected void onResume() {
        super.onResume();
      mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    public void startCounter(View v) {
        // TODO Auto-generated method stub
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        mSensorManager.registerListener(this, mProximity,SensorManager.SENSOR_DELAY_NORMAL);
        disable.setEnabled(true);
    }

    public void stopCounter(View v) {
        // TODO Auto-generated method stub
        mSensorManager.unregisterListener(this);

      //  save.setEnabled(true);
    }

    public void saveData(View v) {
        // TODO Auto-generated method stub

    }




   @Override
    public void onSensorChanged(SensorEvent event) {
       // ImageView img=(ImageView)findViewById(R.id.imageView);
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == 0) {
                //near
                Toast.makeText(getApplicationContext(), "Air Swipe", Toast.LENGTH_SHORT).show();
                img = (ImageView)findViewById(R.id.img);
                if(currentimageindex<IMAGES.length-1) {
                    currentimageindex++;
                    img.setImageResource(IMAGES[currentimageindex]);
                    Animation rotateimage = AnimationUtils.makeInAnimation(this, false);
                    img.startAnimation(rotateimage);
                }
                else
                { currentimageindex=0;
                    img.setImageResource(IMAGES[currentimageindex]);
                    Animation rotateimage = AnimationUtils.makeInAnimation(this, false);
                    img.startAnimation(rotateimage);
                }

               // img.setImageResource(R.drawable.github);
            } else {
                //far
                Toast.makeText(getApplicationContext(), "Picture updated", Toast.LENGTH_SHORT).show();
               // img.setImageResource(R.drawable.facebook);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void playSong() {
        if(val==1)
        {
            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.alife);}
        else if(val==2)
        {
            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.cuppy);}
        else if(val==3)
        {
            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.debussy);}
        else if(val==4)
        {
            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.hereiam);}
        name= (TextView)findViewById(R.id.name);
        name.setText(paths[val]);
        mediaPlayer.start();
    }
    protected void onStop(){
        super.onStop();
        mediaPlayer.release();
        mediaPlayer = null;
        play.setEnabled(true);
        stop.setEnabled(false);
    }
    Runnable mUpdateResults = new Runnable() {
        @Override
        public void run() {
            AnimateandSlideShow();

        }
    };
    Runnable timeUpdateResult = new Runnable() {
        @Override
        public void run() {

            timeUpdate();
        }
    };

    void taskRepeat()
    {   anotherTask();
        mUpdateResults.run();

        timer = new Timer();
        startindex=currentimageindex;
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                mHandler.post(mUpdateResults);

            }

        },3000, period);



    }
    void anotherTask(){
        timeUpdateResult.run();
        timeKeeper=new Timer();
        timeKeeper.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                timehandler.post(timeUpdateResult);

            }

        },1000,1000);
    }
    private void timeUpdate() {

        TextView timedisp=(TextView)findViewById(R.id.timedisp);
        timedisp.setText(String.format("%d min %d sec",min,sec));
        timeNow++;
        min=timeNow/60;
        sec=timeNow%60;
    }


    private void AnimateandSlideShow() {
        img = (ImageView)findViewById(R.id.img);


        if(startindex==0) {
            img.setImageResource(IMAGES[currentimageindex]);
            if (currentimageindex < IMAGES.length - 1) {
                currentimageindex++;
                Animation rotateimage = AnimationUtils.makeInAnimation(this, false);
                img.startAnimation(rotateimage);
            } else {
                timer.cancel();
                timeKeeper.cancel();
                currentimageindex = 0;
                Button s = (Button) findViewById(R.id.start);
                s.setEnabled(true);
                timeNow = 0;
                min = 0;
                sec = 0;

            }
        }
        else {
            img.setImageResource(IMAGES[currentimageindex]);
            if(currentimageindex!=startindex-1)
            {
            if (currentimageindex < IMAGES.length - 1) {
                currentimageindex++;
                Animation rotateimage = AnimationUtils.makeInAnimation(this, false);
                img.startAnimation(rotateimage);
            }
                else
            {
                currentimageindex=0;
                Animation rotateimage = AnimationUtils.makeInAnimation(this, false);
                img.startAnimation(rotateimage);
            }
            }else {
                timer.cancel();
                timeKeeper.cancel();
                currentimageindex = 0;
                Button s = (Button) findViewById(R.id.start);
                s.setEnabled(true);
                timeNow = 0;
                min = 0;
                sec = 0;

            }
        }
    }

}
