package com.smartsudoku;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartsudoku.database.DBHelper;
import com.smartsudoku.database.Record;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScoresFragment extends Fragment {
    LinearLayout scoresLayout;

    public ScoresFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scoresLayout = view.findViewById(R.id.scoresLayout);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        rowParams.setMargins(0, 3, 0, 3);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        DBHelper db = DBHelper.getInstance(view.getContext());
        ArrayList<Record> array = db.getRecords();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        for (Record record : array) {
            LinearLayout row = new LinearLayout(getContext());

            TextView date = new TextView(getContext());
            date.setLayoutParams(textParams);
            date.setPadding(8, 2, 0, 2);
            date.setText(dateFormat.format(new Date(record.date)));
            date.setTextColor(Color.WHITE);
            date.setTextSize(20);
            row.addView(date);

            TextView level = new TextView(getContext());
            level.setLayoutParams(textParams);
            level.setPadding(8, 2, 0, 2);
            level.setText(Integer.toString(record.level));
            level.setTextColor(Color.WHITE);
            level.setTextSize(20);
            row.addView(level);

            TextView time = new TextView(getContext());
            time.setLayoutParams(textParams);
            time.setPadding(8, 2, 0, 2);
            time.setText(timeFormat.format(new Date(record.time)));
            time.setTextColor(Color.WHITE);
            time.setTextSize(20);
            row.addView(time);

            row.setLayoutParams(rowParams);
            scoresLayout.addView(row);
        }
    }
}