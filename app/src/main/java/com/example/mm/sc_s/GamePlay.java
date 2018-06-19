package com.example.mm.sc_s;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mm.sc_s.databinding.ActivityGamePlayBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.view.View.X;
import static android.view.View.Y;

public class GamePlay extends AppCompatActivity {
    public Drawable picture;
    private Drawable[] answers = new Drawable[4];
    private Drawable[] word;
    private int wordSize;

    public GamePlay()
    {
        //TODO Add loading from somewhere
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityGamePlayBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_game_play);
        binding.setTemp(this);

        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.player1);
        BufferedReader buffreader = new BufferedReader(new InputStreamReader(is));
        String line;
        ArrayList<String> text = new ArrayList<>();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.add(line);
            }
        } catch (IOException e) {
            return;
        }

        //get the picture
        int resourceId = res.getIdentifier(text.get(0) , "drawable", getPackageName());
        picture = res.getDrawable(resourceId);
        findViewById(R.id.imageView).setId(resourceId);

        //get the letters from word
        String[] wordLetters = text.get(1).split(",");
        wordSize = wordLetters.length;
        LinearLayout lay_r_capture = findViewById(R.id.ll);
        //LinearLayout.LayoutParams layparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lay_r_capture.setOrientation(LinearLayout.HORIZONTAL);
        lay_r_capture.setGravity(11);
        lay_r_capture.setWeightSum(wordSize);
        //lay_r_capture.setLayoutParams(layparam);

        LinearLayout.LayoutParams lp =  new LinearLayout.LayoutParams(100,LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lp.setMargins(1,1,1,1);
        for(String s : wordLetters)
        {
            ImageView iv = new ImageView(this);
            int resId = res.getIdentifier(s , "drawable", getPackageName());
            iv.setLayoutParams(lp);
            iv.setId(resId);
            iv.setImageDrawable(res.getDrawable(resId));
            lay_r_capture.addView(iv);
        }

        String[] answersNames = text.get(2).split(",");

        for(int i = 0; i < 4; i++)
        {
            int resId = res.getIdentifier(answersNames[i] , "drawable", getPackageName());
            answers[i] = res.getDrawable(resId);
            int originalId = res.getIdentifier("answer"+(i+1) , "id", getPackageName());
            findViewById(originalId).setId(resId);
        }

        binding.setAnswers(answers);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    public void sound(View view)
    {
        String name = getResources().getResourceEntryName(view.getId());
        int soundId = getResources().getIdentifier(name , "raw", getPackageName());
        MediaPlayer ring= MediaPlayer.create(this,soundId);
        ring.start();
    }
}
