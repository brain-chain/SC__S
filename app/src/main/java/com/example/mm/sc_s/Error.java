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
                if(s.equals("")) continue;
                String[] errorParts = s.split(";");
                errors.add(new Error(errorParts[0], errorParts[1]));
            }
        }

        return errors;
    }

    //returns true if the phonemes are approximately equal, false if not
    public static boolean comparePhonemes(String s1, String s2, boolean isVowel)
    {
        //if phonemes are vowels, a & aa would be considered equal, as will n and s (sheva and no-punctuation)
        if(isVowel)
            return s1.charAt(0) == s2.charAt(0) ||
                    s1.charAt(0) == 's' && s2.charAt(0) == 'n' || s1.charAt(0) == 'n' && s2.charAt(0) == 's';

        //if phonemes are consonants, we would want emphasized and non-emphasized letters to be considered equal
        boolean emphasized = s1.replace("e","").equals(s2.replace("e",""));

        //check that the consonant is not a letter that change pronounciation with emphasize ("dagesh")
        boolean allowingEmphasize = !(s1.charAt(0) == 'b' || s1.charAt(0) == 'p' || s1.charAt(0) == 'k');

        return s1.equals(s2) || (emphasized && allowingEmphasize);
    }

    //returns a list of the consonants and vowels which have been mistaken
    public TreeMap<String, String> getPhonemes()
    {
        TreeMap<String, String> phonemes = new TreeMap<>();

        if(!comparePhonemes(consonantError.first, consonantError.second, false))
        {
            phonemes.put("consonant1",consonantError.first);
            phonemes.put("consonant2",consonantError.second);
        }

        if(!comparePhonemes(vowelError.first, vowelError.second, true))
        {
            phonemes.put("vowel1",vowelError.first);
            phonemes.put("vowel2",vowelError.second);
        }

        return phonemes;
    }

}
