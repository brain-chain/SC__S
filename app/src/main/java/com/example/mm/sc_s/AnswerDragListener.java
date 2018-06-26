package com.example.mm.sc_s;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by mm on 26/06/2018.
 */

public class AnswerDragListener implements View.OnDragListener
{
    private String correctAnswer;
    private boolean isSpace = false;
    private Resources res;

    public AnswerDragListener(String s, String correct)
    {
        super();
        correctAnswer = correct;
        if(s.equals("space")) isSpace = true;
    }

    public boolean onDrag(View v, DragEvent event)
    {
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
                if(isSpace)
                    //((ImageView) v).setImageDrawable(R.drawable.a_a);
                return true;
        }
        return false;
    }
}

