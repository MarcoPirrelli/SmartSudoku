package com.smartsudoku;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.smartsudoku.business.Manager;
import com.smartsudoku.business.algorithms.AlgorithmManager;

public class AlgorithmsFragment extends Fragment implements View.OnClickListener {
    Button algHighlight, algApply;
    RadioGroup algRadio;

    public AlgorithmsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_algorithms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        algHighlight = view.findViewById(R.id.algHighlight);
        algHighlight.setOnClickListener(this);
        algApply = view.findViewById(R.id.algApply);
        algApply.setOnClickListener(this);

        algRadio = view.findViewById(R.id.algRadio);
    }

    @Override
    public void onClick(View view) {
        Manager.INSTANCE.clearHighlights();
        int checked = algRadio.getCheckedRadioButtonId();
        if (checked != -1) {
            for(int i = 0; i<= AlgorithmManager.INSTANCE.getMaxAlgs(); i++) {
                if (checked == algRadio.getChildAt(i).getId()) {
                    if (view == algHighlight) {
                        Manager.INSTANCE.where(i);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_algorithmsFragment_to_mainFragment);
                    } else if (view == algApply) {
                        Manager.INSTANCE.apply(i);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_algorithmsFragment_to_mainFragment);
                    }
                    break;
                }
            }
        }
    }
}