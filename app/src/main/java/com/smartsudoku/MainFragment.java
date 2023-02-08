package com.smartsudoku;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.smartsudoku.business.GeneratorWaiter;
import com.smartsudoku.business.Manager;
import com.smartsudoku.database.DBHelper;

import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Fragment implements MenuProvider, View.OnClickListener, GeneratorWaiter {
    DBHelper db;
    Timer timer = null;

    Button eraseButton, undoButton, redoButton;
    ToggleButton penButton, colorButton;
    TextView timerText, finalTime;

    LinearLayout gridLinearLayout, victoryPopup;
    ViewSwitcher inputSwitcher;
    SudokuButton[] sudokuButtons = new SudokuButton[81];
    Button[] digitButtons = new Button[9];
    Button[] colorButtons = new Button[5];

    SudokuButton selected;

    static int[] colors = new int[5];

    static {
        colors[0] = Color.WHITE;
        colors[1] = Color.rgb(255, 200, 200);
        colors[2] = Color.rgb(200, 255, 200);
        colors[3] = Color.rgb(180, 200, 255);
        colors[4] = Color.rgb(255, 255, 200);
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Manager.INSTANCE.setGeneratorWaiter(this);

        db = DBHelper.getInstance(view.getContext());
        requireActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        gridLinearLayout = view.findViewById(R.id.grid);
        createGrid(getResources().getDisplayMetrics().widthPixels);
        victoryPopup = view.findViewById(R.id.generatorPopup);
        victoryPopup.setOnClickListener(this);

        //digit buttons
        digitButtons[0] = view.findViewById(R.id.digit1);
        digitButtons[1] = view.findViewById(R.id.digit2);
        digitButtons[2] = view.findViewById(R.id.digit3);
        digitButtons[3] = view.findViewById(R.id.digit4);
        digitButtons[4] = view.findViewById(R.id.digit5);
        digitButtons[5] = view.findViewById(R.id.digit6);
        digitButtons[6] = view.findViewById(R.id.digit7);
        digitButtons[7] = view.findViewById(R.id.digit8);
        digitButtons[8] = view.findViewById(R.id.digit9);
        for (Button b : digitButtons) {
            b.setOnClickListener(this);
        }

        eraseButton = view.findViewById(R.id.eraseButton);
        eraseButton.setOnClickListener(this);

        penButton = view.findViewById(R.id.penButton);
        penButton.setOnClickListener(this);
        penButton.setChecked(!Manager.inputNotes);

        undoButton = view.findViewById(R.id.undoButton);
        undoButton.setOnClickListener(this);

        redoButton = view.findViewById(R.id.redoButton);
        redoButton.setOnClickListener(this);

        inputSwitcher = view.findViewById(R.id.inputSwitcher);

        colorButton = view.findViewById(R.id.colorButton);
        colorButton.setOnClickListener(this);
        colorButton.setChecked(!Manager.inputColors);
        if (Manager.inputColors)
            inputSwitcher.showNext();

        colorButtons[0] = view.findViewById(R.id.color1);
        colorButtons[1] = view.findViewById(R.id.color2);
        colorButtons[2] = view.findViewById(R.id.color3);
        colorButtons[3] = view.findViewById(R.id.color4);
        colorButtons[4] = view.findViewById(R.id.color5);
        for (int i = 0; i < colors.length; i++) {
            colorButtons[i].setBackgroundColor(colors[i]);
            colorButtons[i].setOnClickListener(this);
        }

        timerText = view.findViewById(R.id.timerText);

        finalTime = view.findViewById(R.id.finalTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Manager.INSTANCE.getTime() == 0) {
            timerText.setText("No timer");
        } else {
            startTimer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(timer!=null){
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        switch (id) {
            case R.id.mmNew: {
                navController.navigate(R.id.action_mainFragment_to_newSudokuFragment);
                return true;
            }
            case R.id.mmAlgorithms: {
                navController.navigate(R.id.action_mainFragment_to_algorithmsFragment);
                return true;
            }
            case R.id.mmScores: {
                navController.navigate(R.id.action_mainFragment_to_scoresFragment);
                return true;
            }
            case R.id.mmSettings: {
                navController.navigate(R.id.action_mainFragment_to_settingsFragment);
                return true;
            }
            case R.id.mmPopulate:{
                db.populateRecords();
                return true;
            }
        }
        return false;
    }

    private void createGrid(int screenWidth) {
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        rowParams.setMargins(0, 3, 0, 3);
        LinearLayout.LayoutParams rowParamsSpecial = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        rowParamsSpecial.setMargins(0, 3, 0, 8);

        for (int i = 0; i < 9; i++) {
            LinearLayout row = new LinearLayout(getContext());
            for (int j = 0; j < 9; j++) {
                sudokuButtons[i * 9 + j] = new SudokuButton(this, screenWidth, this, i, j);
                row.addView(sudokuButtons[i * 9 + j]);
            }
            if (i != 2 && i != 5)
                row.setLayoutParams(rowParams);
            else
                row.setLayoutParams(rowParamsSpecial);
            row.setPadding(0, 0, 0, 0);
            gridLinearLayout.addView(row);
        }
    }


    public void updateAll() {
        for (SudokuButton b : sudokuButtons) {
            b.updateText();
            b.updateColor();
        }
        if (selected != null)
            selected.darken();
    }

    public void clearGrid() {
        Manager.INSTANCE.clearSudoku();
        updateAll();
        updateTime();
    }

    private void undo() {
        Manager.INSTANCE.undo();
        updateAll();
    }

    private void redo() {
        Manager.INSTANCE.redo();
        updateAll();
    }

    private void digitPressed(int n) {
        if (selected != null) {
            Manager.INSTANCE.write(selected.row, selected.col, n);
            updateAll();
        }
    }

    private void colorPressed(int colorId) {
        if (selected != null) {
            selected.setBackgroundColor(colors[colorId]);
            selected.darken();
        }
    }

    private void erase() {
        if (selected != null) {
            Manager.INSTANCE.erase(selected.row, selected.col);
            selected.updateText();
        }
    }

    void victory() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        long time = updateTime();
        if (Manager.INSTANCE.getTime() == 0) {
            timerText.setText("No timer");
        } else {
            long timeS = time / 1000;
            finalTime.setText(String.format("Time: %d:%02d:%02d", timeS / 3600, (timeS % 3600) / 60, (timeS % 60)));

            db.addRecord(Manager.INSTANCE.getLevel(), time);
        }
        victoryPopup.setVisibility(View.VISIBLE);
    }

    void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        }, 0, 1000);
    }

    long updateTime() {
        long time;
        String text;
        if (Manager.INSTANCE.getTime() == 0) {
            time = 0;
            text = "No timer";
        } else {
            time = (System.currentTimeMillis() - Manager.INSTANCE.getTime());
            long timeS = time/1000;
            text = String.format("Time: %d:%02d:%02d", timeS / 3600, (timeS % 3600) / 60, (timeS % 60));
        }
        FragmentActivity activity = getActivity();
        if (activity != null)
            activity.runOnUiThread(() -> timerText.setText(text));
        return time;
    }

    @Override
    public void onClick(View v) {
        if (victoryPopup.getVisibility() == View.VISIBLE) {
            victoryPopup.setVisibility(View.INVISIBLE);
            clearGrid();
        } else
            for (int i = 0; i < 9; i++) {
                if (v == digitButtons[i]) {
                    Manager.INSTANCE.clearHighlights();
                    digitPressed(i + 1);
                    if (Manager.INSTANCE.hasSolution() && Manager.INSTANCE.isFinished())
                        victory();
                    return;
                }

            }
        if (v instanceof SudokuButton) {
            SudokuButton clickedButton = (SudokuButton) v;
            //if it's the selected button, unselect it
            if (clickedButton != selected) {
                if (selected != null)
                    selected.lighten();
                selected = clickedButton;
                clickedButton.darken();
            }
            return;
        } else if (v == penButton)
            Manager.inputNotes = !Manager.inputNotes;
        else if (v == undoButton)
            undo();
        else if (v == redoButton)
            redo();
        else if (v == colorButton) {
            Manager.inputColors = !Manager.inputColors;
            inputSwitcher.showNext();
        } else {
            //color buttons
            for (int i = 0; i < colorButtons.length; i++) {
                if (v == colorButtons[i]) {
                    colorPressed(i);
                    return;
                }
            }
        }

        if (v == eraseButton) {
            Manager.INSTANCE.clearHighlights();
            erase();
        }
    }

    @Override
    public void generationCompleted() {
        Activity activity = getActivity();
        if(activity!=null)
            activity.runOnUiThread(() -> Toast.makeText(getContext(), "Sudoku generation finished", Toast.LENGTH_SHORT).show());
    }
}