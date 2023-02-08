package com.smartsudoku;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.smartsudoku.business.Manager;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    Switch switchCheckMistakes, switchAutoUpdateNotes;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchCheckMistakes = view.findViewById(R.id.switchCheckMistakes);
        switchAutoUpdateNotes = view.findViewById(R.id.switchAutoUpdateNotes);

        switchCheckMistakes.setOnCheckedChangeListener(this);
        switchAutoUpdateNotes.setOnCheckedChangeListener(this);

        switchCheckMistakes.setChecked(Manager.checkForMistakes);
        switchAutoUpdateNotes.setChecked(Manager.autoUpdateNotes);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == switchCheckMistakes)
            Manager.checkForMistakes = b;
        else if (compoundButton == switchAutoUpdateNotes)
            Manager.autoUpdateNotes = b;
    }
}