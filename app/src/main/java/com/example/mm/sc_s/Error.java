package com.example.mm.sc_s;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mm on 18/06/2018.
 */

public class Error
{
    public Pair<String,String> consonantError;
    public Pair<String,String> vowelError;

    public Error(String correct, String wrong)
    {
        String realAnswer[] = correct.split("_");
        String wrongAnswer[] = wrong.split("_");

        consonantError = new Pair<>(realAnswer[1],wrongAnswer[1]);
        vowelError = new Pair<>(realAnswer[0],wrongAnswer[0]);
    }

    //@override
    // returns the correct and wrong answer
    public String toString()
    {
        return vowelError.first+"_"+consonantError.first+";"+vowelError.second+"_"+consonantError.second;
    }

    public static ArrayList<Error> toError(ArrayList<String> error)
    {
        ArrayList<Error> errors = new ArrayList<>();

        if(error != null)
        {
            for (String s : error)
            {
                String[] errorParts = s.split(";");
                errors.add(new Error(errorParts[0], errorParts[1]));
            }
        }

        return errors;
    }

    public TreeMap<String, String> getPhonemes(String[] s)
    {
        TreeMap<String, String> phonemes = new TreeMap<>();

        if(!consonantError.first.equals(consonantError.second))
        {
            if (consonantError.first.equals(s[1])) phonemes.put("consonant",consonantError.first);
            if (consonantError.second.equals(s[1])) phonemes.put("consonant",consonantError.second);
        }

        if(!vowelError.first.equals(vowelError.second))
        {
            if (vowelError.first.equals(s[0])) phonemes.put("vowel",vowelError.first);
            if (vowelError.second.equals(s[0])) phonemes.put("vowel",vowelError.second);
        }

        return phonemes;
    }

}
