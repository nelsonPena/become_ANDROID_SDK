package com.becomedigital.sdk.identity.becomedigitalsdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.becomedigital.sdk.identity.becomedigitalsdk.utils.DisplayResolution;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;
import static androidx.navigation.Navigation.findNavController;

public class DocumentVideoCaptureCameraO extends Fragment {
    private final static String TAG = DocumentVideoCaptureCameraO.class.getSimpleName ( );
    private static Camera camera;
    private ImageButton btnCapture;
    private static MediaRecorder mRecorder;
    private String urlVideoFile;
    private ShowCamera showCamera;
    private boolean isCameraActive = false;
    private static TextView textTimerRecord;
    private static TextView textTimerToStart;
    private static ImageView imgAnimatorIndocator;
    private TextView textTittleVideo;
    private static int countdownToStart;
    public static int countdownRecording;
    private boolean recording;
    private CountDownTimer countDownTimerRecord;
    private CountDownTimer countDownTimerGeneral;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate (R.layout.document_video_capture_camera_o, container, false);
    }

    private void initialSetups() {
        textTittleVideo = getActivity ( ).findViewById (R.id.textTittleVideo);
        // controls video
        imgAnimatorIndocator = getActivity ( ).findViewById (R.id.img_animation_indicator);
        textTimerRecord = getActivity ( ).findViewById (R.id.textTimerRecord);
        textTimerToStart = getActivity ( ).findViewById (R.id.textTimerToStart);
        // controls image
        btnCapture = getActivity ( ).findViewById (R.id.btnCapture);
        activateCamera ( ); // ativate camera capture

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).changeColorToolbar (true);
        initialSetups ( );
        final Animation animScale = AnimationUtils.loadAnimation (getActivity ( ), R.anim.anim_scale);
        if (getArguments ( ).getBoolean ("isVideoCapture")) {
            countdownRecording = 8;
            countdownToStart = 4;
            initCoundown ( );
            Glide.with (this)
                    .load (R.drawable.recording)
                    .into (imgAnimatorIndocator);
        } else {
            ImageView imgProgressVideo = getActivity ( ).findViewById (R.id.imgProgressVideo);
            imgProgressVideo.setImageResource (R.drawable.progress_document_contrast);
            btnCapture.setVisibility (View.VISIBLE);
            btnCapture.setOnTouchListener ((v, event) -> {
                switch (event.getAction ( )) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation (animScale);
                        v.setScaleX ((float) 1.2);
                        v.setScaleY ((float) 1.2);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.clearAnimation ( );
                        v.setScaleX ((float) 1.0);
                        v.setScaleY ((float) 1.0);
                        btnCapture.setEnabled (false);
                        captureImage ( );
                        break;
                }
                return false;
            });

            ImageView imgReference = getActivity ( ).findViewById (R.id.imgReferenceVideo);
            imgReference.setImageResource (R.drawable.back_document);// cuadro captura documento
            String documentFace = "";
            ImageView imgDocReference = getActivity ( ).findViewById (R.id.imgDocReference);

            if (getArguments ( ).getBoolean ("isFront")) {
                documentFace = getActivity ( ).getString (R.string.text_tittle_intro_doc_front);
                if (((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedDocument ( ) == MyApplication.typeDocument.PASSPORT) {
                    imgDocReference.setImageResource (R.drawable.reference_capture_passport);
                }
            } else {
                imgDocReference.setImageResource (R.drawable.reference_document_back);
                documentFace = getActivity ( ).getString (R.string.text_tittle_intro_doc);
            }
            imgDocReference.setVisibility (View.VISIBLE);
            new CountDownTimer (3000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    Animation animFadeOut = AnimationUtils.loadAnimation (getActivity ( ), R.anim.fade_out);
                    imgDocReference.startAnimation (animFadeOut);
                    imgDocReference.setVisibility (View.INVISIBLE);
                }
            }.start ( );

            String selectedDocument = ((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedDocument ( ) == MyApplication.typeDocument.LICENSE ? getActivity ( ).getString (R.string.text_license) : ((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedDocument ( ) == MyApplication.typeDocument.PASSPORT ? getActivity ( ).getString (R.string.text_passport) : getActivity ( ).getString (R.string.text_dni_selec_document);
            textTittleVideo.setTextSize (18);
            textTittleVideo.setText (String.format ("%s\n %s\n %s", documentFace, selectedDocument, ((MyApplication) getActivity ( ).getApplicationContext ( )).getSelectedCountry ( )));
        }
    }

    @Override
    public void onPause() {
        super.onPause ( );
        relaceCamera ( );
    }


    // region photo capture

    private void setOrientationCameraAndFocusMode() {

        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters ( );
            if (this.getResources ( ).getConfiguration ( ).orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set ("orientation", "portrait");
                camera.setDisplayOrientation (90);
                parameters.setRotation (90);
            } else {
                parameters.set ("orientation", "landscape");
                camera.setDisplayOrientation (0);
                parameters.setRotation (0);
            }

            if (!getArguments ( ).getBoolean ("isVideoCapture")) {
                parameters.setFocusMode (Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            camera.setParameters (parameters);
        }
    }

    private void captureImage() {
        if (camera != null) {
            camera.takePicture (null, null, mPictureCallback);
        }
    }

    private Camera.PictureCallback mPictureCallback = (data, camera) -> {

        try {
            btnCapture.setEnabled (false);
            camera.stopPreview ( );
            int rotationDegrees = DisplayResolution.calculateRotationDegrees (data, getActivity ( ));
            Bundle bundle = new Bundle ( );
            ((MyApplication) getActivity ( ).getApplicationContext ( )).setDataToTransfer (data);
            bundle.putInt ("rotationDegrees", rotationDegrees);
            bundle.putBoolean ("isFront", getArguments ( ).getBoolean ("isFront"));
            findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionPreviewImage, bundle);
        } catch (Exception e) {
            ((MainBDIV) getActivity ( )).setResultError (e.getLocalizedMessage ( ));
        }
    };

    private static boolean whichCamera = true;

    private void relaceCamera() {
        if (camera != null) {
            frameLayout.removeView (showCamera);
            camera.release ( );
            camera = null;
            whichCamera = !whichCamera;
        }
    }

    private static SurfaceHolder sHolder;
    private FrameLayout frameLayout;

    private void activateCamera() {
        if (getArguments ( ).getBoolean ("isVideoCapture")) {
            camera = Camera.open (CAMERA_FACING_FRONT);
        } else {
            camera = Camera.open ( );
        }

        Camera.Parameters parameters = camera.getParameters ( );
//        parameters.setPictureSize(640, 480);
        parameters.setJpegQuality (12);

        camera.setParameters (parameters);
        showCamera = new ShowCamera (getActivity ( ), camera);
        frameLayout = getActivity ( ).findViewById (R.id.fragment_container_camera);
        frameLayout.addView (showCamera);
    }

    //endregion


    // region video recorder

    private void initCoundown() {
        countDownTimerRecord = new CountDownTimer (3000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.d (TAG, "time to record video:" + millisUntilFinished);
            }

            public void onFinish() {
                Log.d (TAG, "time: done");
                startRecording ( );
                recording = true;
            }
        }.start ( );
        countDownTimerGeneral = new CountDownTimer (11000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.d (TAG, "time: " + millisUntilFinished);
                countdownToStart--;
                if (countdownToStart <= 0) { // inicia la visualizacion de la cuenta regresiva de grabado
                    if (countdownRecording >= 0) {
                        countdownRecording--;
                        textTimerRecord.setVisibility (View.VISIBLE);
                        String textCoundownRecording = "0" + countdownRecording;
                        textTimerRecord.setText (textCoundownRecording); // asigna el valor al contador inferior
                        imgAnimatorIndocator.setVisibility (View.VISIBLE);
                        textTimerToStart.setVisibility (View.INVISIBLE);
                    }
                } else {
                    String counDownToStratS = String.valueOf (countdownToStart);
                    textTimerToStart.setText (counDownToStratS); // asigna el valor al contador en el medio
                }
            }

            public void onFinish() {
                Log.d (TAG, "time: done");
                stopRecording ( );
                recording = false;
                findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionOkRecordSelecDocument);
            }
        }.start ( );
    }

    private void startRecording() {
        if (prepareVideoRecorder ( )) {
            mRecorder.start ( );
        } else {
            releaseMediaRecorder ( );
            findNavController (getActivity ( ), R.id.nav_host_fragment).popBackStack ( );
        }
    }

    private void stopRecording() {
        mRecorder.stop ( );  // stop the recording
        releaseMediaRecorder ( ); // release the MediaRecorder object
        camera.lock ( );         // take camera access back from MediaRecorder
        ((MyApplication) getActivity ( ).getApplication ( )).setUrlVideo (urlVideoFile);
    }

    private String getFileName_CustomFormat() {
        SimpleDateFormat sdfDate = new SimpleDateFormat ("yyyy-MM-dd HH_mm_ss", Locale.getDefault ( ));
        Date now = new Date ( );
        String strDate = sdfDate.format (now);
        return strDate;
    }

    private boolean prepareVideoRecorder() {
        try {
            mRecorder = new MediaRecorder ( );
            camera.unlock ( );
            mRecorder.setCamera (camera);
            mRecorder.setAudioSource (MediaRecorder.AudioSource.DEFAULT);
            mRecorder.setVideoSource (MediaRecorder.VideoSource.DEFAULT);
            CamcorderProfile profile = CamcorderProfile.get (Camera.CameraInfo.CAMERA_FACING_FRONT, CamcorderProfile.QUALITY_480P);
            mRecorder.setProfile (profile);
            mRecorder.setOrientationHint (270);
            mRecorder.setVideoFrameRate (20);
            mRecorder.setVideoEncodingBitRate (1000000);
            urlVideoFile = getString (R.string.path_internal_files) + getFileName_CustomFormat ( ) + ".mp4";
            mRecorder.setOutputFile (urlVideoFile);
            mRecorder.setVideoSize (640, 480);
            mRecorder.setPreviewDisplay (sHolder.getSurface ( ));

            mRecorder.prepare ( );
        } catch (Exception e) {
            Toast.makeText (getActivity ( ), "exception: " + e.getMessage ( ), Toast.LENGTH_LONG).show ( );
            releaseMediaRecorder ( );
            ((MainBDIV) getActivity ( )).setResultError (e.getLocalizedMessage ( ));
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.reset ( );   // clear recorder configuration
            mRecorder.release ( ); // release the recorder object
            mRecorder = null;
            camera.lock ( );           // lock camera for later use
        }
    }

    // endregion

    private class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {
        private Camera camera;
        private SurfaceHolder holder;
        private final String TAG = ShowCamera.class.getSimpleName ( );

        public ShowCamera(Context context, Camera camera) {
            super (context);
            this.camera = camera;
            holder = getHolder ( );
            holder.addCallback (this);
            holder.setType (SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            sHolder = holder;
            try {
                setOrientationCameraAndFocusMode ( );
                camera.setPreviewDisplay (holder);
                camera.startPreview ( );
                isCameraActive = true;
            } catch (IOException e) {
                ((MainBDIV) getActivity ( )).setResultError (e.getMessage ( ));
                e.printStackTrace ( );
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d (TAG, "change");
            if (isCameraActive) {
                int mPreviewHeight = camera.getParameters ( ).getPreviewSize ( ).height;
                int mPreviewWidth = camera.getParameters ( ).getPreviewSize ( ).width;
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            shutdown ( );
        }

        private void shutdown() {
            // Release MediaRecorder and especially the Camera as it's a shared
            // object that can be used by other applications
            if (getArguments ( ).getBoolean ("isVideoCapture")) {
                countDownTimerGeneral.cancel ( );
                countDownTimerRecord.cancel ( );
            }
            if (recording) {
                mRecorder.stop ( );
                recording = false;
                mRecorder.release ( );
            }
            camera.stopPreview ( );
            camera.release ( );

            // once the objects have been released they can't be reused
            mRecorder = null;
            camera = null;
        }
    }

}
