package com.example.mm.sc_s;

import android.content.Intent;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by mm on 18/06/2018.
 */

public class Question
{
    private Resources resources;
    private int id;

    private ArrayList<String> readFromFile(int id)
    {
        InputStream is = resources.openRawResource(id);
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

    public Question(Resources res, int questionId)
    {
        resources = res;
        ArrayList<String> text = readFromFile(questionId);


    }

    /**
     *  The function presents a multiple choice question to user,
     *  returns null if he was right, else his error type
     */
    public Error play()
    {

        return null;
    }
}
