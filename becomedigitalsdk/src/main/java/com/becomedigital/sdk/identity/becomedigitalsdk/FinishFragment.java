package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinishFragment extends Fragment {


    public FinishFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_finish, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        Button btnUpload = getActivity ().findViewById (R.id.btnUpload);
        btnUpload.setOnTouchListener ((v, event) -> {
            switch (event.getAction ( )) {
                case MotionEvent.ACTION_DOWN:
                    getActivity ( ).runOnUiThread (() -> {
                        ((MainBDIV) getActivity ( )).displayLoader ( );
                    });
                    break;
                case MotionEvent.ACTION_UP:
                    ((MainBDIV) getActivity ( )).addDataServer();
                    break;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).setIsHomeActivity (true);
        ((MainBDIV) getActivity ( )).changeColorToolbar (false);
    }
}
