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
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.view.View.X;
import static android.view.View.Y;
import static java.lang.System.exit;

public class GamePlay extends AppCompatActivity {
    public Drawable picture;
    private int questionId;
    private Drawable[] answers = new Drawable[4];
    private Drawable[] word;
    private int wordSize;
    private boolean answeredRight = false;
    private String correctAnswer;
    private String m_error;
    private Resources res;
    private String correctId;

    private ArrayList<String> readFromFile(int id)
    {
        Resources res = getResources();

        InputStream is = res.openRawResource(id);
        BufferedReader buffreader = new BufferedReader(new InputStreamReader(is));
        String line;
        ArrayList<String> text = new ArrayList<>();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.add(line);
            }
        } catch (IOException e) {
            return null;
        }

        try {
            buffreader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res = getResources();

        if(savedInstanceState == null)
        {
            questionId = R.raw.q_1;
        } else
        {
            savedInstanceState.getInt("currentQuestion");
        }

        ActivityGamePlayBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_game_play);
        initializeGame(questionId);

        binding.setTemp(this);
        binding.setAnswers(answers);

    }

    private void initializeGame(final int questionId)
    {

        ArrayList<String> text = readFromFile(questionId);

        //get the picture
        int resourceId = res.getIdentifier(text.get(0), "drawable", getPackageName());
        picture = res.getDrawable(resourceId);
        ((ImageView) findViewById(R.id.imageView)).setImageDrawable(picture);

        //create the word
        Word myWord = new Word(text.get(0));

        //get the letters from word
        String[] wordLetters = text.get(2).split(",");
        wordSize = wordLetters.length;
        LinearLayout lay_r_capture = findViewById(R.id.ll);
        lay_r_capture.removeAllViews();
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
                    int numberOfQuestions = 3;
                    int maxId = R.raw.q_1+numberOfQuestions-1;
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

                            boolean isSpace = res.getResourceEntryName(v.getId()).equals("space");
                            if(isSpace && correctId.equals(res.getResourceEntryName(view.getId())))
                            {
                                int answerId = res.getIdentifier(correctAnswer, "drawable", getPackageName());
                                ((ImageView) v).setImageDrawable(res.getDrawable(answerId));
                                answeredRight = true;
                                //TODO add some happy stupid audio
                                if(questionId+1 <= maxId) initializeGame(questionId+1);
                                else finish();
                                return false;
                            }
                            return true;
                    }
                    return false;
                }
            });
        }

        String[] answersNames = text.get(3).split(",");

        //generating the answers
        for (i = 0; i < 4; i++) {
            if(correctAnswer.equals(answersNames[i]))
                correctId = "answer" + Integer.toString(i+1);
            int resId = res.getIdentifier(answersNames[i], "drawable", getPackageName());
            answers[i] = res.getDrawable(resId);
            int originalId = res.getIdentifier("answer" + (i + 1), "id", getPackageName());
            ImageView iv = findViewById(originalId);
            iv.setImageDrawable(answers[i]);
            iv.setOnTouchListener(
                    new View.OnTouchListener() {
                        private boolean touched = false;

                        @Override
                        public boolean onTouch(View view, MotionEvent event) {
                            if (!touched) {
                                //sound(view);
                                touched = true;
                            }
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN: {
                                    String imgName = res.getResourceEntryName(view.getId());
                                    ClipData data = ClipData.newPlainText("name", imgName);
                                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                                    view.startDrag(data, shadowBuilder, view, 0);
                                    view.setVisibility(View.INVISIBLE);
                                    return true;
                                }
                                case MotionEvent.ACTION_UP:
                                case MotionEvent.ACTION_CANCEL: {
                                    touched = false;
                                    if (!correctAnswer.equals(res.getResourceEntryName(view.getId()))) {
                                        view.setVisibility(View.VISIBLE);
                                        return true;
                                    }
                                    return false;
                                }
                                default:
                                    break;
                            }
                            return true;
                        }
                    });
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);

        outState.putInt("currentQuestion", questionId);
        outState.putString("errorType", m_error);

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
