package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import static androidx.navigation.Navigation.findNavController;


public class IntroSelectDocumentTypeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_intro_select_document_type, container, false);
    }


    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).setIsHomeActivity (false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        Button btnContinue = getActivity ( ).findViewById (R.id.btnContinueInfo);
        btnContinue.setOnClickListener (view1 -> findNavController (view1).navigate (R.id.SelectCountryAction));
        ((MainBDIV) getActivity ( )).changeColorToolbar (false);
        String split = getString (R.string.splitValidationTypes);
        String[] validationTypesSubs = ((MyApplication) getActivity ( ).getApplicationContext ( )).getValidationTypes ( ).split (split);
        LinearLayout lPassport = getActivity ( ).findViewById (R.id.lPassportInfo);
        LinearLayout lDNI = getActivity ( ).findViewById (R.id.lDNIInfo);
        LinearLayout lLicense = getActivity ( ).findViewById (R.id.lLicenseInfo);
        for (String validationTypesSub : validationTypesSubs) {
            switch (validationTypesSub) {
                case "DNI":
                    lDNI.setVisibility (View.VISIBLE);
                    break;
                case "PASSPORT":
                    lPassport.setVisibility (View.VISIBLE);
                    break;
                case "LICENSE":
                    lLicense.setVisibility (View.VISIBLE);
                    break;
            }
        }
    }
}
