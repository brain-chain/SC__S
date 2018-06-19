package com.example.mm.sc_s;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.mm.sc_s.databinding.ActivityGamePlayBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GamePlay extends AppCompatActivity {
    public String pictureName;
    public Drawable picture;
    private String[] answers;
    private String[] word;


    public GamePlay()
    {
        //TODO Add loading from somewhere
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        InputStream is = res.openRawResource(R.raw.player1);
        BufferedReader buffreader = new BufferedReader(new InputStreamReader(is));
        String line;
        ArrayList<String> text = new ArrayList<String>();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.add(line);
            }
        } catch (IOException e) {
            return;
        }

        pictureName="R.drawable."+text.get(0);
        int resourceId = res.getIdentifier(text.get(0) , "drawable", getPackageName());
        picture = res.getDrawable(resourceId);
        ActivityGamePlayBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_game_play);
        binding.setTemp(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }
}
