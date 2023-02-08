package com.smartsudoku;

import android.os.AsyncTask;
import com.smartsudoku.business.Manager;

public class GenerateTask extends AsyncTask<Void, Void, Void> {
    int level;

    public GenerateTask(int level) {
        this.level = level;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Manager.INSTANCE.generateSudoku(level);
        return null;
    }
}
