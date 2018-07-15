package com.example.mm.sc_s;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.SharedPreferences;
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
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static android.view.View.X;
import static android.view.View.Y;
import static java.lang.System.exit;

public class GamePlay extends AppCompatActivity {
    public Drawable picture;
    private int m_id;
    private Drawable[] m_answers = new Drawable[4];
    private Drawable[] word;
    private int wordSize;
    private ArrayList<Question> m_questions;
    private ArrayList<Question> m_futureQuestions;
    private boolean answeredRight = false;
    private String correctAnswer;
    private ArrayList<Error> m_error;
    private Resources res;
    private boolean gameFinished = false;
    private int soundId;
    private String[] m_answersNames;
    private int numberOfQuestions;


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

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
 //       preferences.edit().clear().commit();

        m_questions = getQuestionList(preferences.getString("questions", null));
        m_futureQuestions = getQuestionList(preferences.getString("futureQuestions", null));

        m_error = Error.toError(getList(preferences.getString("error", null)));


        //means no previous state was saved
        if(m_futureQuestions == null)
        {
            createQuestionList();
        }


        ActivityGamePlayBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_game_play);
        initializeGame(pickQuestion());

        binding.setTemp(this);
        binding.setAnswers(m_answers);

    }

    private ArrayList<Question> getQuestionList(String futureQuestions)
    {
        if(futureQuestions == null) return null;

        ArrayList<String> questionStrings = getList(futureQuestions);
        ArrayList<Question> questions = new ArrayList<>();

        for(String s : questionStrings)
        {
            if(s.equals("")) continue;
            questions.add(createQuestion(s));
        }

        return questions.isEmpty() ? null : questions;
    }

    private void createQuestionList()
    {
        Field[] fields=R.raw.class.getFields();
        m_questions = new ArrayList<>();
        m_futureQuestions = new ArrayList<>();

        //add questions to list
        for(Field f : fields)
        {
            String file = f.getName();
            if(file.startsWith("q"))
            {
                numberOfQuestions++;
                Question q = createQuestion(file);
                m_questions.add(q);
                m_futureQuestions.add(q);
            }
        }
    }

    private Question createQuestion(String file)
    {
        int questionId = res.getIdentifier(file, "raw", getPackageName());
        ArrayList<String> questionData = readFromFile(questionId);

        String[] syllabs = questionData.get(2).split(",");
        int correctId = Integer.parseInt(questionData.get(1)) - 1;

        String correctAnswer = syllabs[correctId];
        syllabs[correctId] = "space";

        return new Question(questionId, file, questionData.get(0), correctAnswer, syllabs);
    }

    private ArrayList<String> getList(String q)
    {
        if(q == null) return null;

        String[] questions = q.split(" ");

        return new ArrayList<String>(Arrays.asList(questions));
    }

    private Question pickQuestion()
    {
        if(m_futureQuestions.isEmpty()) return null;

        // pick randomly from future questions
        int rand = (int)(Math.random()*m_futureQuestions.size());

        Question q = m_futureQuestions.get(rand);
        m_futureQuestions.remove(q);

        return q;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initializeGame(Question q)
    {

        if(Integer.parseInt(q.getFileName().replace("q_","")) > 42)
            System.out.println("bla");
        //get the picture
        int resourceId = res.getIdentifier(q.getName(), "drawable", getPackageName());
        picture = res.getDrawable(resourceId);
        ((ImageView) findViewById(R.id.imageView)).setImageDrawable(picture);

        //get the sound for picture
        //soundId = res.getIdentifier(q.getName(), "raw", getPackageName());

        //get the letters of the word
        String[] wordLetters = q.getWord();
        wordSize = wordLetters.length;

        //get the correct answer
        correctAnswer = q.getAnswer();

        LinearLayout lay_r_capture = findViewById(R.id.ll);
        lay_r_capture.removeAllViews();
        lay_r_capture.setOrientation(LinearLayout.HORIZONTAL);
        lay_r_capture.setGravity(11);
        lay_r_capture.setWeightSum(wordSize);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        lp.setMargins(1, 1, 1, 1);
        int i = 0;

        for (String s : wordLetters)
        {
            if(s.equals("n_e0"))
                i += 0;
            i++;
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
                            if(isSpace)
                            {
                                String usersAnswer = getAnswerName(view);
                                if(SyllabComparator.compareSyllabs(correctAnswer,usersAnswer))
                                {
                                    int answerId = res.getIdentifier(correctAnswer, "drawable", getPackageName());
                                    ((ImageView) v).setImageDrawable(res.getDrawable(answerId));
                                    answeredRight = true;

                                    //remove ancient errors from list - so no questions will be added based on them
                                    if(!m_error.isEmpty())
                                        m_error.remove(m_error.get(0));

                                    //TODO add some happy stupid audio
                                    Question nextQuestion = pickQuestion();

                                    if (nextQuestion != null)
                                    {
                                        initializeGame(nextQuestion);
                                    } else {
                                        gameFinished = true;
                                        //TODO add some end Screen and stupider audio
                                        finish();
                                    }
                                    return false;
                                }

                                else
                                {
                                    view.setVisibility(View.VISIBLE);
                                    Error e = new Error(correctAnswer, usersAnswer);
                                    m_error.add(e);
                                    if(m_error.size() > 3)
                                        m_error.remove(m_error.get(0));
                                }

                                updateQuestionsByError();
                                return false;
                            }
                            return true;
                    }
                    return false;
                }
            });
        }

        m_answersNames = q.getAnswers();

        //generating the answers
        for (i = 0; i < 4; i++) {
            int resId = res.getIdentifier(m_answersNames[i], "drawable", getPackageName());

            m_answers[i] = res.getDrawable(resId);

            int originalId = res.getIdentifier("answer" + (i + 1), "id", getPackageName());
            ImageView iv = findViewById(originalId);
            iv.setImageDrawable(m_answers[i]);
            iv.setVisibility(View.VISIBLE);
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
                                    return false;
                                }
                                case MotionEvent.ACTION_CANCEL:
                                case MotionEvent.ACTION_UP:
                                    {
//                                    touched = false;
//                                    if (!SyllabComparator.compareSyllabs(correctAnswer,getAnswerName((ImageView)view)))
//                                    {
                                        view.setVisibility(View.VISIBLE);
//                                        return false;
//                                    }
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
//!SyllabComparator.compareSyllabs(correctAnswer,res.getResourceEntryName(view.getId()))

    //gets the answer's syllab from the ImageView
    private String getAnswerName(ImageView view)
    {
        String ans = res.getResourceEntryName(view.getId());
        return m_answersNames[Integer.parseInt(ans.replace("answer","")) - 1];
    }

    private void updateQuestionsByError()
    {
        for(Question q : m_questions)
        {
            if(q.getAnswer().equals(correctAnswer)) break;
            for (int i = m_error.size(); i > 0; i--)
            {
                Error e = m_error.get(i-1);

                if(q.addAnswer(e.getPhonemes()))
                {
                    if(!m_futureQuestions.contains(q))
                    {
                        m_futureQuestions.add(q);
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }


    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(gameFinished)
        {
            editor.clear();
            editor.commit();
        }

        //keep current question id
        editor.putInt("currentQuestion", m_id);

        //keep lists of questions, future questions, and errors
        String questions = "";
        for(Question q : m_questions)
        {
            questions = questions.concat(q.getFileName() + " ");
        }
        String futureQuestions = "";
        for(Question q : m_futureQuestions)
        {
            futureQuestions = futureQuestions.concat(q.getFileName() + " ");
        }

        String errors = "";
        for(Error e : m_error)
        {
            errors = errors.concat(e.toString() + " ");
        }

        editor.putString("questions", questions);
        editor.putString("futureQuestions", futureQuestions);
        editor.putString("error", errors);

        editor.commit();

    }

    public void sound(View view)
    {
        MediaPlayer ring= MediaPlayer.create(this,soundId);
        ring.start();
    }
}
