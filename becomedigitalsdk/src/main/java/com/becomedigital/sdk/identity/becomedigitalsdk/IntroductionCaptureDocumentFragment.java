package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.becomedigital.sdk.identity.becomedigitalsdk.utils.CompressImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroductionCaptureDocumentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.fragment_capture_introduction_document, container, false);
    }

    private final int RESULT_LOAD_IMG = 100;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        ((MainBDIV) getActivity ( )).changeColorToolbar (false);
        Button btnCaptureDoc = getActivity ( ).findViewById (R.id.btnCaptureDoc);

        btnCaptureDoc.setOnClickListener (view1 -> {
            if (arePermissionsGranted ( )) {
                requestPermissions (PERMISSIONS, REQUEST_PERMISSIONS);
            } else {
                goToDocumentCapture ( );
            }
        });


        Button btnGalery = getActivity ( ).findViewById (R.id.btnGalery);
        if (!((MyApplication) getActivity ( ).getApplicationContext ( )).isAllowLibraryLoading ( )) {
            btnGalery.setEnabled (false);
            btnGalery.setTextColor (getResources ( ).getColor (R.color.grayLigth));
        } else {
            btnGalery.setOnClickListener (view12 -> {
                Intent photoPickerIntent = new Intent (Intent.ACTION_PICK);
                photoPickerIntent.setType ("image/*");
                int RESULT_LOAD_IMG = 100;
                startActivityForResult (photoPickerIntent, RESULT_LOAD_IMG);
            });
        }

        ImageView imgReference = getActivity ( ).findViewById (R.id.imgReference);
        TextView textDocType = getActivity ( ).findViewById (R.id.textDocType);
        // valida el tipo de selccion y carga la introduccion
        if (((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedDocument ( ) == MyApplication.typeDocument.DNI || ((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedDocument ( ) == MyApplication.typeDocument.LICENSE) {
            if (!getArguments ( ).getBoolean ("isFront")) {
                imgReference.setImageResource (R.drawable.document_reference_back);
            }
            if (((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedDocument ( ) == MyApplication.typeDocument.DNI)
                textDocType.setText (getString (R.string.text_dni_selec_document));
            else
                textDocType.setText (getString (R.string.text_license));

        } else if (((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedDocument ( ) == MyApplication.typeDocument.PASSPORT) {
            imgReference.setImageResource (R.drawable.passport_reference);
            textDocType.setText (getString (R.string.text_passport));
        }

        TextView textTittleIntro = getActivity ( ).findViewById (R.id.textTittleIntro);
        if (!getArguments ( ).getBoolean ("isFront")) {
            textTittleIntro.setText (getString (R.string.text_tittle_intro_doc));
        }

        TextView textCountry = getActivity ( ).findViewById (R.id.textCountry);
        textCountry.setText (((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedCountry ( ));
    }

    private final int REQUEST_PERMISSIONS = 34;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private Boolean arePermissionsGranted() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (checkSelfPermission (getActivity ( ), PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (resultCode == getActivity ( ).RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMG) {
                Uri imageUri = data.getData ( );
                String pathFile = CompressImage.getRealPathFromURIData (imageUri, getActivity ( ));
                Bundle bundle = new Bundle ( );
                bundle.putString ("pathImage", pathFile);
                bundle.putBoolean ("isFront", getArguments ( ).getBoolean ("isFront"));
                findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionPreviewPiker, bundle);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionsGranted ( )) {
                ((MainBDIV) getActivity ( )).setResultError ("denied permits for the camera");
            } else {
                getActivity ( ).runOnUiThread (this::goToDocumentCapture);
            }
        }
    }

    private void goToDocumentCapture() {
        Bundle bundle = new Bundle ( );
        bundle.putBoolean ("isFront", getArguments ( ).getBoolean ("isFront"));
        bundle.putBoolean ("isVideoCapture", false);// salta a la captura
        findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.captureVideoAction, bundle);
    }

    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).changeColorToolbar (false);
    }
}
