package com.example.mm.sc_s;

import android.content.Intent;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Created by mm on 18/06/2018.
 */

public class Question
{
    private int id;
    private String name;
    private String fileName;
    private String[] word;
    private String correctAnswer;
    private ArrayList<String> m_answers;

    public Question(int questionId, String file, String wordName, String trueAnswer, String[] syllabs)
    {
        id = questionId;
        fileName = file;
        name = wordName;
        correctAnswer = trueAnswer;

        m_answers = new ArrayList<>();
        m_answers.add(correctAnswer);

        word = syllabs;
    }

    public String getAnswer() { return correctAnswer; }

    public int getId() { return id; }

    public String getName() { return name; }


    public boolean addAnswer(TreeMap<String,String> phonemes)
    {
        Field[] fields=R.drawable.class.getFields();

        String[] answerPhonemes = correctAnswer.split("_");

        for (String k : phonemes.keySet())
        {
            String value = phonemes.get(k);

            //if no difference between answer and error phonemes, continue
            if(k.charAt(0) == 'c' && phonemes.get(k).equals(answerPhonemes[1])) continue;
            if(k.charAt(0) == 'v' && phonemes.get(k).equals(answerPhonemes[0])) continue;

            value = k.charAt(0) == 'c' ? "_"+value : value;
            for(Field f : fields)
            {
                if (f.getName().contains(value) && !f.getName().equals(correctAnswer))
                {
                    m_answers.add(f.getName());

                    //keep the number of answers 4
                    if (m_answers.size() > 4)
                        //index 1 is the least recently added answer, therefore we can ommit it
                        m_answers.remove(1);
                    return true;
                }
            }
        }
        return false;
    }

    public String[] getWord()
    {
        return word;
    }

    public String[] getAnswers()
    {
        while (m_answers.size() < 4)
        {
            Field[] fields = R.drawable.class.getFields();

            int rand = (int) (Math.random() * fields.length);

            String file = fields[rand].getName();

            // if the file is a syllab, add it to answers
            if (isValidAnswer(file))
                m_answers.add(file);
        }

        String[] answers = new String[4];

        answers[(int) (Math.random() * 4)] = correctAnswer;

        int j = 1;
        for(int i = 0; i < 4; )
        {
            if (answers[i] != null)
            {
                i++;
                continue;
            }

            answers[i] = m_answers.get(j++);
        }

        return answers;
    }

    public boolean isValidAnswer(String s)
    {
        String[] vowels = {"a","aa","e","ee","i","ii","o","oo","u","uu","s","sn","n"};
        ArrayList<String> vow = new ArrayList<String>(Arrays.asList(vowels));

        String[] splitAnswer = s.split("_");

        return vow.contains(splitAnswer[0]) && splitAnswer.length == 2;
    }

    public String getFileName()
    {
        return fileName;
    }
}
