package com.smartsudoku;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import com.smartsudoku.business.Cell;
import com.smartsudoku.business.Manager;

public class SudokuButton extends androidx.appcompat.widget.AppCompatButton {
    int screenWidth;
    float bigNumber, smallNumber;
    int row, col;
    GradientDrawable background;

    public SudokuButton(Fragment parentFragment, int screenWidth, OnClickListener listener, int row, int col) {
        super(parentFragment.getContext());

        this.row = row;
        this.col = col;
        this.screenWidth = screenWidth;
        bigNumber = ((float) screenWidth) / 35;
        smallNumber = ((float) screenWidth) / 102;

        background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);

        updateColor();

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        buttonParams.setMargins(3, 0, 3, 0);
        LinearLayout.LayoutParams buttonParamsSpecial = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        buttonParamsSpecial.setMargins(3, 0, 8, 0);
        if (col != 2 && col != 5)
            setLayoutParams(buttonParams);
        else
            setLayoutParams(buttonParamsSpecial);

        setPadding(0, 0, 0, 0);

        updateText();
        setTypeface(Typeface.MONOSPACE);

        setOnClickListener(listener);
    }

    public void updateText() {
        Cell cell = Manager.currentSudoku.getCell(row, col);
        if (cell.isEmpty()) {
            setText("");
            setTextSize(bigNumber);
        } else if (cell.isMark()) {
            setText(Integer.toString(cell.getVal()));
            setTextSize(bigNumber);
            if (Manager.checkForMistakes && Manager.INSTANCE.isWrong(row,col))
                setTextColor(Color.RED);
            else if (cell.isFixed())
                setTextColor(Color.BLACK);
            else
                setTextColor(Color.BLUE);
        } else {
            StringBuilder string = new StringBuilder();
            for (int n = 1; n <= 9; n++) {
                if (cell.contains(n))
                    string.append(n);
                else
                    string.append(" ");

                if (n == 3 || n == 6)
                    string.append("\n");
                else if (n != 9)
                    string.append(" ");
            }
            setText(string.toString());
            setTextSize(smallNumber);
            setTextColor(Color.GRAY);
        }
    }

    public void updateColor() {
        background.setColor(getColor());
        if (Manager.currentSudoku.getCell(row, col).getHighlighted())
            background.setStroke(7, Color.YELLOW);
        else {
            background.setStroke(0, Color.WHITE);
        }
        setBackground(background);
    }

    int getColor() {
        return Manager.currentSudoku.getColor(row, col);
    }

    public void setBackgroundColor(int color) {
        Manager.INSTANCE.setColor(row, col, Color.red(color), Color.green(color), Color.blue(color));
        updateColor();
    }

    public void darken() {
        background.setColor(ColorUtils.blendARGB(getColor(), Color.BLACK, 0.2f));
        setBackground(background);
    }

    public void lighten() {
        background.setColor(getColor());
        setBackground(background);
    }
}
