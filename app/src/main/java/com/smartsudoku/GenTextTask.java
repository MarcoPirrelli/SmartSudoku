package com.smartsudoku;

import android.app.Activity;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.TimerTask;

public class GenTextTask extends TimerTask {
    Activity activity;
    TextView textView;

    public GenTextTask(Activity activity, TextView textView) {
        this.activity = activity;
        this.textView = textView;
    }

    @Override
    public void run() {
        if (activity != null)
            activity.runOnUiThread(() -> {
                String text = textView.getText().toString();
                if(text.equals("Generating..."))
                    textView.setText("Generating");
                else
                    textView.setText(text + ".");
            });
    }
}
