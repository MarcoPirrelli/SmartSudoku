package com.smartsudoku;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.smartsudoku.business.GeneratorWaiter;
import com.smartsudoku.business.Manager;
import com.smartsudoku.business.algorithms.AlgorithmManager;

import java.util.Timer;

public class newSudokuFragment extends Fragment implements View.OnClickListener, GeneratorWaiter {
    Button newGenerate;
    ToggleButton newToggleInit;
    RadioGroup newRadio;

    LinearLayout generatorPopup;
    TextView generatingText;
    Timer timer;
    GenerateTask generateTask;
    GenTextTask genTextTask;

    public newSudokuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_sudoku, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Manager.INSTANCE.setGeneratorWaiter(this);

        newGenerate = view.findViewById(R.id.newGenerate);
        newGenerate.setOnClickListener(this);

        newToggleInit = view.findViewById(R.id.newToggleInit);
        newToggleInit.setOnClickListener(this);
        newToggleInit.setChecked(Manager.customInit);

        newRadio = view.findViewById(R.id.newRadio);
        generatorPopup = view.findViewById(R.id.generatorPopup);
        generatingText = view.findViewById(R.id.generatingText);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Manager.INSTANCE.getGenerating())
            startTimer();
        else if(Manager.INSTANCE.getGeneratedGrid()!=null){
            newGenerate.setText("Use generated sudoku");
            if(timer!=null){
                timer.cancel();
                timer.purge();
            }
            generatorPopup.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(genTextTask!=null){
            genTextTask.cancel();
        }
        if(timer!=null){
            timer.cancel();
            timer.purge();
        }
    }

    private void generate(int level){
        if(Manager.INSTANCE.getGenerating())
            return;
        generateTask = new GenerateTask(level);
        generateTask.execute();

        startTimer();
    }

    private void startTimer(){
        generatingText.setText("Generating");
        generatorPopup.setVisibility(View.VISIBLE);
        if(timer!=null){
            timer.cancel();
            timer.purge();
        }
        timer = new Timer();
        genTextTask = new GenTextTask(getActivity(), generatingText);
        timer.scheduleAtFixedRate(genTextTask, 1000, 1000);
    }

    @Override
    public void generationCompleted() {
        if(timer!=null){
            timer.cancel();
            timer.purge();
        }
        generatorPopup.setVisibility(View.INVISIBLE);
        if(Manager.INSTANCE.getGeneratedGrid()!=null){
            newGenerate.setText("Use generated sudoku");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == newToggleInit) {
            if (Manager.customInit)
                Manager.INSTANCE.findSolvedGrid();
            else
                Manager.INSTANCE.clearSudoku();
            Manager.customInit = !Manager.customInit;
            NavHostFragment.findNavController(this).navigate(R.id.action_newSudokuFragment_to_mainFragment);
        }
        else if (v == newGenerate) {
            Manager.customInit = false;

            if(Manager.INSTANCE.getGeneratedGrid()!=null) {
                Manager.INSTANCE.applyGenerated();
                NavHostFragment.findNavController(this).navigate(R.id.action_newSudokuFragment_to_mainFragment);
            }
            else {
                int checked = newRadio.getCheckedRadioButtonId();
                if (checked != -1) {
                    for (int i = 0; i < AlgorithmManager.INSTANCE.getMaxLevels(); i++) {
                        if (checked == newRadio.getChildAt(i).getId()) {
                            generate(i);
                        }
                    }
                }
            }
        }
    }

}