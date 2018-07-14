package com.example.mm.sc_s;


/**
 * Created by mm on 15/07/2018.
 */

// A class for comparisons of phonemes and syllabs
public class SyllabComparator
{
    //compares phonemes to allow errors with no consequence in pronounciation (kamats-patah, etc.)
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

    //compare syllabs to check the answer
    public static boolean compareSyllabs(String s1, String s2)
    {
        String[] syllab1 = s1.split("_");
        String[] syllab2 = s2.split("_");

        return comparePhonemes(syllab1[0],syllab2[0],true) && comparePhonemes(syllab1[1],syllab2[1],false);
    }
}
