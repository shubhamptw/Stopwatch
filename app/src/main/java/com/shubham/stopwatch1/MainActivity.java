package com.shubham.stopwatch1;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore ref;
    DocumentReference dRef;
    TextView textView;
    ImageView start;
    ImageView second;
    ImageView reset;
    TextView waiting;
    int count=0;
    long timeBuff=0;
    long updateTime=0;
    long milli=0;
    int buttonState;
    int lapsCount;
    boolean mStarted;
    long startTime,oldDegree;
    boolean running;
    Handler handler;
    Map<String,Object> notes=new HashMap<>();
    long seconds, minutes, milliSeconds ;
    String value;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);
        waiting=findViewById(R.id.waiting);
        start=findViewById(R.id.start);
        reset=findViewById(R.id.imageView);
        second=findViewById(R.id.second);
        handler= new Handler();
        start.setEnabled(true);
        buttonState=1;
        ref=FirebaseFirestore.getInstance();
        dRef=ref.collection("collection").document("document");
        notes.put("Time","0");
        dRef.set(notes);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStarted=true;
                handler.postDelayed(runnable,10L);
                start.setImageResource(R.drawable.pause);
                startTime=System.currentTimeMillis();
                notes.put("Time","1");
                dRef.set(notes);
                start.setVisibility(View.GONE);
                waiting.setVisibility(View.VISIBLE);

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                milli = 0L ;
                startTime = 0L ;
                timeBuff = 0L ;
                updateTime = 0L ;
                seconds = 0 ;
                minutes = 0 ;
                milliSeconds = 0 ;
                rotate(oldDegree,360);
                oldDegree=0;
                textView.setText(String.format("%02d:%02d:%02d",0,0,0));
                notes.put("Time","0");
                dRef.set(notes);
                buttonState=1;
                reset.setVisibility(View.GONE);
                start.setImageResource(R.drawable.play);
                start.setVisibility(View.VISIBLE);
            }
        });

        dRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.getString("Time").equals("2"))
                { reset.setVisibility(View.VISIBLE);
                    waiting.setVisibility(View.GONE);
                    mStarted=false;

                }

            }
        });

    }

    private  void rotate(float fromDegree, float toDegree){
        RotateAnimation rotateAnimation=new RotateAnimation(fromDegree,toDegree,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setFillAfter(true);
        second.startAnimation(rotateAnimation);
    }
    private final Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(mStarted){
                milli=System.currentTimeMillis()-startTime;
                updateTime=timeBuff+milli;
                seconds=updateTime/1000;
                textView.setText(String.format("%02d:%02d:%02d",seconds/60,seconds%60,updateTime%100));
                rotate(oldDegree,updateTime*3/500);
                oldDegree=updateTime*3/500;
                handler.postDelayed(runnable,10L);

            }

        }
    };



}

