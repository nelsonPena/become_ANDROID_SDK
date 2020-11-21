package com.becomedigital.sdk.identity.becomedigitalsdkiv;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.becomedigital.sdk.identity.becomedigitalsdk.MainBDIV;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeCallBackManager;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeInterfaseCallback;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.BecomeResponseManager;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.LoginError;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private BecomeCallBackManager mCallbackManager = BecomeCallBackManager.createNew ( );

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        Bitmap decodeResource = BitmapFactory.decodeResource (getResources ( ), R.drawable.become_icon);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ( );

        decodeResource.compress (Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray ( );

        TextView textResponse = findViewById (R.id.textReponse);
        EditText textClientSecret = findViewById (R.id.cliensecretText);
        EditText textClientId = findViewById (R.id.clienidText);
        EditText textContractId = findViewById (R.id.ContractIdText);
        EditText textVlidationType = findViewById (R.id.validationType);
        Button btnAut = findViewById (R.id.btnAuth);
        btnAut.setOnClickListener (view -> {
            String validatiopnTypes = textVlidationType.getText ( ).toString ( ).isEmpty ( ) ? "VIDEO/DNI" : textVlidationType.getText ( ).toString ( );
            String clientSecret = textClientSecret.getText ( ).toString ( ).isEmpty ( ) ? "FKLDM63GPH89TISBXNZ4YJUE57WRQA25" : textClientSecret.getText ( ).toString ( );
            String clientId = textClientId.getText ( ).toString ( ).isEmpty ( ) ? "acc_demo" : textClientId.getText ( ).toString ( );
            String contractId = textContractId.getText ( ).toString ( ).isEmpty ( ) ? "2" : textContractId.getText ( ).toString ( );

            BecomeResponseManager.getInstance ( ).startAutentication (MainActivity.this,
                    new BDIVConfig (clientId,
                            clientSecret,
                            contractId,
                            validatiopnTypes,
                            true,
                            byteArray
                    ));
            BecomeResponseManager.getInstance ( ).registerCallback (mCallbackManager, new BecomeInterfaseCallback ( ) {
                @Override
                public void onSuccess(final ResponseIV responseIV) {
                    String id = responseIV.getId ( );
                    String created_at = responseIV.getCreated_at ( );
                    String company = responseIV.getCompany ( );
                    String fullname = responseIV.getFullname ( );
                    String birth = responseIV.getBirth ( );
                    String document_type = responseIV.getDocument_type ( );
                    String document_number = responseIV.getDocument_number ( );
                    Boolean face_match = responseIV.getFace_match ( );
                    Boolean template = responseIV.getTemplate ( );
                    Boolean alteration = responseIV.getAlteration ( );
                    Boolean watch_list = responseIV.getWatch_list ( );
                    String comply_advantage_result = responseIV.getComply_advantage_result ( );
                    String comply_advantage_url = responseIV.getComply_advantage_url ( );
                    String verification_status = responseIV.getVerification_status ( );
                    String message = responseIV.getMessage ( );
                    Integer responseStatus = responseIV.getResponseStatus ( );
                    String textFinal = "id: "  +
                             "\ncreated_at: " + created_at +
                             "\ncompany: " + company +
                             "\nfullname: " + fullname +
                             "\nbirth: " + birth +
                             "\ndocument_type: " + document_type +
                             "\ndocument_number: " + document_number +
                             "\nface_match: " + face_match +
                             "\ntemplate: " + template +
                             "\nalteration: " + alteration +
                             "\nwatch_list: " + watch_list +
                             "\ncomply_advantage_result: " + comply_advantage_result +
                             "\ncomply_advantage_url: " + comply_advantage_url +
                             "\nverification_status: " + verification_status +
                             "\nmessage: " + message +
                             "\nresponseStatus: " + responseStatus;

                    textResponse.setText (textFinal);
                    Log.d ("responseIV", textFinal);
                }

                @Override
                public void onCancel() {
                    textResponse.setText ("Cancelado por el usuario ");

                }

                @Override
                public void onError(LoginError pLoginError) {
                    textResponse.setText (pLoginError.getMessage ( ));
                    Log.d ("Error", pLoginError.getMessage ( ));
                }

            });
        });

    }
}
