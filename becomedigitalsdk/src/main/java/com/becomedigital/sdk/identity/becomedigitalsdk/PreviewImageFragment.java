package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static androidx.navigation.Navigation.findNavController;


/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewImageFragment extends Fragment {


    public PreviewImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_preview_image, container, false);
    }

    Button btnIsOkImage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        ((MainBDIV) getActivity ( )).changeColorToolbar (true);
        ImageView imgToPreview = getActivity ( ).findViewById (R.id.imgToPreview);

        btnIsOkImage = getActivity ( ).findViewById (R.id.btnOkImage);
        Button btnRetry = getActivity ( ).findViewById (R.id.btnRetry);

            String pathToFile = getArguments ( ).getString ("pathImage");
            File imgFile = new File (pathToFile);

            if (imgFile.exists ( )) {
                Bitmap myBitmap = BitmapFactory.decodeFile (imgFile.getAbsolutePath ( ));
                imgToPreview.setImageBitmap (myBitmap);
            }

            btnRetry.setOnClickListener (view1 -> {
                    if (imgFile.exists ( )) {
                        imgFile.delete ( );
                    }
                findNavController (view).popBackStack ( );

            });
            btnIsOkImage.setOnClickListener (view12 -> {
                if (getArguments ( ).getBoolean ("isFront")) {
                    ((MyApplication) getActivity ( ).getApplication ( )).setUrlDocFront (pathToFile);
                } else {
                    ((MyApplication) getActivity ( ).getApplication ( )).setUrlDocBack (pathToFile);
                }
                navigate ( );
            });

    }

    private void navigate() {
        if (((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedDocument ( ) == MyApplication.typeDocument.PASSPORT) {
            findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionFinish);
        } else {
            Bundle bundle = new Bundle ( );
            bundle.putBoolean ("isFront", false);
            if (getArguments ( ).getBoolean ("isFront")) {
                bundle.putBoolean ("isFront", false);
                findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionCaptureBackDocument, bundle);
            } else {
                findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionFinish);
            }
        }
    }

}
