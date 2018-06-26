package com.example.mm.sc_s;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mm.sc_s.databinding.ActivityGamePlayBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.view.View.X;
import static android.view.View.Y;
import static java.lang.System.exit;

public class GamePlay extends AppCompatActivity {
    public Drawable picture;
    private Drawable[] answers = new Drawable[4];
    private Drawable[] word;
    private int wordSize;
    private boolean answeredRight = false;
    private String correctAnswer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGamePlayBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_game_play);
        binding.setTemp(this);

        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.player1);
        BufferedReader buffreader = new BufferedReader(new InputStreamReader(is));
        String line;
        ArrayList<String> text = new ArrayList<>();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.add(line);
            }
        } catch (IOException e) {
            return;
        }

        //get the picture
        int resourceId = res.getIdentifier(text.get(0), "drawable", getPackageName());
        picture = res.getDrawable(resourceId);
        findViewById(R.id.imageView).setId(resourceId);

        //create the word
        Word myWord = new Word(text.get(0));

        //get the letters from word
        String[] wordLetters = text.get(2).split(",");
        wordSize = wordLetters.length;
        LinearLayout lay_r_capture = findViewById(R.id.ll);
        lay_r_capture.setOrientation(LinearLayout.HORIZONTAL);
        lay_r_capture.setGravity(11);
        lay_r_capture.setWeightSum(wordSize);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lp.setMargins(1, 1, 1, 1);
        int i = 0;
        int spaceIndex = Integer.parseInt(text.get(1));
        for (String s : wordLetters)
        {
            i++;
            if(i == spaceIndex)
            {
                correctAnswer = s;
                s = "space";
            }
            ImageView iv = new ImageView(this);
            int resId = res.getIdentifier(s, "drawable", getPackageName());
            iv.setLayoutParams(lp);
            iv.setId(resId);
            iv.setImageDrawable(res.getDrawable(resId));
            lay_r_capture.addView(iv);
            iv.setOnDragListener(new View.OnDragListener() {
                    @SuppressLint("ResourceType")
                    @TargetApi(Build.VERSION_CODES.M)
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        int action = event.getAction();
                        switch (event.getAction()) {
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                break;
                            case DragEvent.ACTION_DRAG_ENDED:
                                break;
                            case DragEvent.ACTION_DROP:
                                // Dropped, reassign View to ViewGroup
                                ImageView view = (ImageView) event.getLocalState();
                                ViewGroup owner = (ViewGroup) view.getParent();
                                //owner.removeView(view);
                                Resources res = getResources();
                                boolean isSpace = res.getResourceEntryName(v.getId()).equals("space");
                                String dsf = res.getResourceEntryName(view.getId());
                                if(isSpace && dsf.equals(correctAnswer))
                                {
                                    ((ImageView) v).setImageDrawable(res.getDrawable(view.getId()));
                                    answeredRight = true;
                                    return false;
                                }
                                return true;
                        }
                        return false;
                    }
                });
        }

            String[] answersNames = text.get(3).split(",");

            for (i = 0; i < 4; i++) {
                int resId = res.getIdentifier(answersNames[i], "drawable", getPackageName());
                answers[i] = res.getDrawable(resId);
                int originalId = res.getIdentifier("answer" + (i + 1), "id", getPackageName());
                ImageView iv = findViewById(originalId);
                iv.setId(resId);
                iv.setOnTouchListener(
                        new View.OnTouchListener() {
                            private boolean touched = false;

                            @Override
                            public boolean onTouch(View view, MotionEvent event) {
                                if (!touched) {
                                    //sound(view);
                                    touched = true;
                                }
                                if (event.getAction() == MotionEvent.ACTION_DOWN)
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN: {
                                            String imgName = getResources().getResourceEntryName(view.getId());
                                            ClipData data = ClipData.newPlainText("name", imgName);
                                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                                            view.startDrag(data, shadowBuilder, view, 0);
                                            view.setVisibility(View.INVISIBLE);
                                            return true;
                                        }
                                        case MotionEvent.ACTION_UP:
                                            exit(-1);
                                            int i = 5;
                                        case MotionEvent.ACTION_CANCEL: {
                                            exit(-7);
                                            touched = false;
                                            if(!answeredRight) view.setVisibility(View.VISIBLE);
                                            return true;
                                        }
                                        default:
                                            break;
                                    }
                                return true;
                            }
                        });

                binding.setAnswers(answers);

            }
        }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    public void sound(View view)
    {
        Resources res = getResources();
        String name = res.getResourceEntryName(view.getId());
        int soundId = res.getIdentifier(name , "raw", getPackageName());
        MediaPlayer ring= MediaPlayer.create(this,soundId);
        ring.start();
    }
}
