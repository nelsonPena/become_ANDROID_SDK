package com.becomedigital.sdk.identity.becomedigitalsdk;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.becomedigital.sdk.identity.becomedigitalsdk.callback.AsynchronousTask;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeCallBackManager;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;
import com.becomedigital.sdk.identity.becomedigitalsdk.services.ValidateStatusRest;
import com.becomedigital.sdk.identity.becomedigitalsdk.utils.LoadCountries;
import com.bumptech.glide.Glide;

import static androidx.navigation.Navigation.findNavController;

public class MainBDIV extends AppCompatActivity implements AsynchronousTask {
    public static final String TAG = MainBDIV.class.getSimpleName ( );
    public static final String KEY_ERROR = "ErrorMessage";
    private static ValidateStatusRest autService;
    private static Context mContext;
    private static int progress;
    private BDIVConfig config;
    public Intent mData = new Intent ( );
    public androidx.appcompat.widget.Toolbar toolbar;
    private boolean isViewCloseAction;
    private final int USERRESPONSE = 0;
    private final int INITAUTHRESPONSE = 1;
    private final int ADDDATARESPONSE = 2;
    private FrameLayout frameInit;
    private TextView textInfoServer;
    private String urlVGlobal;
    private boolean isHomeActivity = true;
    private CountDownTimer countdownToGetdata;

    public static Context getAppContext() {
        return mContext;
    }

    private ImageButton imgBtnCancel;
    private ImageButton imgBtnBack;

    private BecomeCallBackManager mCallbackManager = BecomeCallBackManager.createNew ( );

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate (bundle);
        setContentView (R.layout.activity_main_bdiv);

        frameInit = findViewById (R.id.frameLoaderInit);
        textInfoServer = findViewById (R.id.text_info_server);
        mContext = getApplicationContext ( );
        autService = new ValidateStatusRest ( );
        ImageView imgLoader = findViewById (R.id.imgLoader);// loader inicial
        Glide.with (this)
                .load (R.drawable.load_init)
                .into (imgLoader);
        getExtrasAndValidateConfig ( ); // get data and validate input config parceable
        // carga el logo del usuario
        toolbar = findViewById (R.id.toolbar);
        if (getSupportActionBar ( ) != null) {
            getSupportActionBar ( ).hide ( );
        }
        ImageView imgToolbar = toolbar.findViewById (R.id.imgUser);
        imgBtnCancel = toolbar.findViewById (R.id.btnCancel);
        imgBtnCancel.setOnClickListener (view -> {
            findNavController (MainBDIV.this, R.id.nav_host_fragment).navigate (R.id.cancelAction);
            imgBtnCancel.setVisibility (View.INVISIBLE);
            isViewCloseAction = true;
        });
        if (this.config.getCustomerLogo ( ) != null) {
            imgToolbar.setImageBitmap (BitmapFactory.decodeByteArray (config.getCustomerLogo ( ), 0, config.getCustomerLogo ( ).length));
        }

        imgBtnBack = toolbar.findViewById (R.id.btnBack);
        imgBtnBack.setOnClickListener (view -> {
            onBackPressed ( );
        });

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback (true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                findNavController (MainBDIV.this, R.id.nav_host_fragment).popBackStack ( );
                if (isViewCloseAction)
                    imgBtnCancel.setVisibility (View.VISIBLE);
            }
        };
        getOnBackPressedDispatcher ( ).addCallback (this, callback);

    }

    public void changeColorToolbar(Boolean isDark) {
        if (isDark) {
            toolbar.setBackgroundColor (getResources ( ).getColor (R.color.black));
            imgBtnCancel.setImageDrawable (getResources ( ).getDrawable (R.drawable.close__icon_w));
            imgBtnBack.setImageDrawable (getResources ( ).getDrawable (R.drawable.back_icon_w));
        } else {
            toolbar.setBackgroundColor (getResources ( ).getColor (R.color.white));
            imgBtnCancel.setImageDrawable (getResources ( ).getDrawable (R.drawable.icon_cancel_black));
            imgBtnBack.setImageDrawable (getResources ( ).getDrawable (R.drawable.back_icon_dark34x28_2x));
        }
    }

    public void showImgBtnCancel() {
        imgBtnCancel.setVisibility (View.VISIBLE);
    }

    private void getExtrasAndValidateConfig() { // valida data input user
        if (getIntent ( ).getExtras ( ) != null) {
            this.config = (BDIVConfig) getIntent ( ).getSerializableExtra ("BDIVConfig");
            if (config != null) {
                String split = getString (R.string.splitValidationTypes);
                String[] validationTypesSubs = config.getValidationTypes ( ).split (split);
                if (this.config.getClienId ( ).isEmpty ( )) {
                    setResulLoginError ("ClienId parameters cannot be empty");
                    return;
                } else if (this.config.getClientSecret ( ).isEmpty ( )) {
                    setResulLoginError ("ClientSecret parameters cannot be empty");
                    return;
                } else if (this.config.getContractId ( ).isEmpty ( )) {
                    setResulLoginError ("ContractId parameters cannot be empty");
                    return;
                }else if (this.config.getUserId ( ).isEmpty ( )) {
                    setResulLoginError ("UserId parameters cannot be empty");
                    return;
                }else if (validationTypesSubs.length == 0) {
                    setResulLoginError ("The validationTypes parameter cannot be empty");
                    return;
                } else if (validationTypesSubs.length == 1) {
                    if (validationTypesSubs[0].equals ("VIDEO")) {
                        setResulLoginError ("The process cannot be initialized with video only");
                        return;
                    } else if (!validationTypesSubs[0].equals ("VIDEO")
                            && !validationTypesSubs[0].equals ("DNI")
                            && !validationTypesSubs[0].equals ("PASSPORT")
                            && !validationTypesSubs[0].equals ("LICENSE")) {
                        setResulLoginError ("Input parameters are wrong");
                        return;
                    }
                }
                // autenticarse
                LoadCountries.loadCountries (this);
                autService.getAuth (this, this.config.getClienId ( ), this.config.getClientSecret ( ), this);
                ((MyApplication) getApplication ( )).setClientId (this.config.getClienId ( ));
                ((MyApplication) getApplication ( )).setClientSecret (this.config.getClientSecret ( ));
                ((MyApplication) getApplication ( )).setContractId (this.config.getContractId ( ));
                ((MyApplication) getApplication ( )).setUser_id(this.config.getUserId());
                ((MyApplication) getApplication ( )).setValidationTypes (this.config.getValidationTypes ( ));
                ((MyApplication) getApplication ( )).setAllowLibraryLoading (this.config.isAllowLibraryLoading ( ));
            }

        }
    }


    //region server transactions

    private boolean isOkResponse = false;

    @Override
    public void onReceiveResultsTransaction(ResponseIV responseIV, int transactionId) {
        runOnUiThread (() -> {

            if (transactionId == INITAUTHRESPONSE) {
                Animation animFadeOut = AnimationUtils.loadAnimation (this, R.anim.fade_out);
                frameInit.startAnimation (animFadeOut);
                frameInit.setVisibility (View.GONE);
                ((MyApplication) getAppContext ( )).setAccess_token (responseIV.getMessage ( ));
            }

            if (transactionId == USERRESPONSE) {
                if (responseIV.getResponseStatus ( ) == ResponseIV.SUCCES) {
                    isOkResponse = true;
                    getWindow ( ).clearFlags (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    countdownToGetdata.cancel ( );
                    mData.putExtra ("ResponseIV", (Parcelable) responseIV);
                    setResult (RESULT_OK, mData);
                    finish ( );
                } else {
                    countdownToGetdata.cancel ( );
                    setResultError (responseIV.getMessage ( ));
                }
            }

            if (transactionId == ADDDATARESPONSE) {
                if (responseIV.getResponseStatus ( ) == ResponseIV.SUCCES) {
                    textInfoServer.setText (getResources ( ).getString (R.string.text_progress_inteligence));
                    urlVGlobal = responseIV.getMessage ( );
                    initCounDownGetData (responseIV.getMessage ( ));
                } else {
                    setResultError (responseIV.getMessage ( ));
                }
            }
            Log.d (TAG, responseIV.toString ( ));
        });
    }

    @Override
    public void onErrorTransaction(String errorMsn) {
        getWindow ( ).clearFlags (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        countdownToGetdata.cancel ( );
        mData.putExtra (KEY_ERROR, errorMsn);
        setResult (RESULT_FIRST_USER, mData);
        finish ( );
    }

    public void displayLoader() {
        getWindow ( ).setFlags (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        frameInit.setVisibility (View.VISIBLE);
        textInfoServer.setText (getString (R.string.text_info_upload));
    }

    public void addDataServer() {
        textInfoServer.setText (getString (R.string.text_info_upload));
        autService.addDataServer (this, this);
    }

    private void initCounDownGetData(final String urlGetData) {
        final int[] countTime = {0};
        final int[] countBefore = {10};
        countdownToGetdata = new CountDownTimer (160000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.d (TAG, "time of the process: " + millisUntilFinished);
                countTime[0]++;
                if (!isOkResponse && countTime[0] > countBefore[0]) {
                    countBefore[0] = countTime[0] + 10;
                    autService.getDataAutentication (urlGetData, MainBDIV.this, MainBDIV.this);
                }
            }

            public void onFinish() {
                runOnUiThread (() -> {
                    getWindow ( ).clearFlags (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    textInfoServer.setText (getString (R.string.text_time_out_msn));
                    Button btnClose = findViewById (R.id.btnCloseTO);
                    Button btnRetry = findViewById (R.id.btnRetryTO);
                    btnRetry.setVisibility (View.VISIBLE);
                    btnRetry.setOnClickListener (view -> {
                        getWindow ( ).setFlags (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        countTime[0] = 0;
                        countBefore[0] = 10;
                        btnRetry.setVisibility (View.GONE);
                        btnClose.setVisibility (View.GONE);
                        initCounDownGetData (urlVGlobal);
                        textInfoServer.setText (getString (R.string.retry_send_text));
                    });


                    btnClose.setVisibility (View.VISIBLE);
                    btnClose.setOnClickListener (view -> setResultError ("Time out"));
                    Log.d (TAG, "time: done");
                });
            }
        }.start ( );
    }

    //endregion

    //region app response
    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        this.mCallbackManager.onActivityResult (i, i2, intent);
        super.onActivityResult (i, i2, intent);
    }

    private void setResulLoginError(String msnErr) {
        mData.putExtra (KEY_ERROR, msnErr);
        setResult (RESULT_FIRST_USER, mData);
        finish ( );
    }

    public void setResultError(String msnErr) {
        mData.putExtra (KEY_ERROR, msnErr);
        setResult (RESULT_FIRST_USER, mData);
        finish ( );
    }

    public void setResulUserCanceled() {
        setResult (RESULT_CANCELED, mData);
        finish ( );
    }

    public void setIsHomeActivity(boolean isHomeActivity) {
        this.isHomeActivity = isHomeActivity;
        imgBtnBack.setVisibility (isHomeActivity ? View.GONE : View.VISIBLE);
    }

    //endregion


    @Override
    public void onBackPressed() {
        if (!isHomeActivity)
            super.onBackPressed ( );
    }

}
