package com.becomedigital.sdk.identity.becomedigitalsdk.services;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.becomedigital.sdk.identity.becomedigitalsdk.MyApplication;
import com.becomedigital.sdk.identity.becomedigitalsdk.R;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.AsynchronousTask;
import com.becomedigital.sdk.identity.becomedigitalsdk.callback.SharedParameters;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.ResponseIV;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ValidateStatusRest {
    /* access modifiers changed from: private */
    public static final String TAG = ValidateStatusRest.class.getSimpleName();
    private final int USERRESPONSE = 0;
    private final int INITAUTHRESPONSE = 1;
    private final int ADDDATARESPONSE = 2;

    public void getAuth(final Activity activity, String clientID, String clientSecret, final AsynchronousTask asynchronousTask) {

        AsyncTask.execute(() -> {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                String serverUrl = preferences.getString(SharedParameters.URL_AUTH, SharedParameters.url_auth);

                MediaType JSON = MediaType.parse("application/json");
                JSONObject json = new JSONObject();
                json.put("client_id", clientID);
                json.put("client_secret", clientSecret);

                String jsonString = json.toString();
                Log.d("JSON SEND:", jsonString);
                RequestBody body = RequestBody.create(JSON, jsonString);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS).build();

                Request request = new Request.Builder()
                        .url(serverUrl)
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = new JSONObject(jsonData);
                            if (!Jobject.has("msg")) {
                                asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.SUCCES, Jobject.getString("access_token")), INITAUTHRESPONSE);

                            } else {
                                asynchronousTask.onErrorTransaction(Jobject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        }
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                e.printStackTrace();
            }
        });
    }

    private File video = null;
    private File document1 = null;
    private File document2 = null;

    public void addDataServer(final Activity activity, final AsynchronousTask asynchronousTask) {
        AsyncTask.execute(() -> {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity); // get url
                String serverUrl = preferences.getString(SharedParameters.URL_ADD_DATA, SharedParameters.url_add_data);
                String split = activity.getString(R.string.splitValidationTypes);
                String[] validationTypesSubs = ((MyApplication) activity.getApplicationContext()).getValidationTypes().split(split);
                boolean containsVideo = false;
                boolean isPassword = false;

                for (String validationTypesSub : validationTypesSubs) {
                    if (validationTypesSub.equals("VIDEO")) {
                        containsVideo = true;
                    }
                }
                if (((MyApplication) activity.getApplicationContext()).getSelectedDocument() == MyApplication.typeDocument.PASSPORT) {
                    isPassword = true;
                }
                RequestBody requestBody = null;
                ContentResolver contentResolver = activity.getContentResolver();
                if (containsVideo) {
                    requestBody = addDocumentsAndVideo(isPassword, activity);
                } else {
                    requestBody = addDocuments(isPassword, activity);
                }


                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS).build();

                Request request = new Request.Builder()
                        .header("Authorization", "Bearer " + ((MyApplication) activity.getApplicationContext()).getAccess_token())
                        .url(serverUrl)
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = new JSONObject(jsonData);
                            if (Jobject.has("message")) {
                                if (Jobject.getString("message").equals("El recurso fue creado")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.SUCCES, Jobject.getString("url_resource")), ADDDATARESPONSE);
                                } else {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("message")), ADDDATARESPONSE);
                                }
                            } else {
                                if (Jobject.has("msg")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("msg")), ADDDATARESPONSE);
                                }
                                if (Jobject.has("error")) {
                                    asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, Jobject.getString("error")), ADDDATARESPONSE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        }

//                        if (video.exists ( ))
//                            video.delete ( );
//                        if (document1.exists ( ))
//                            document1.delete ( );
//                        if (document2.exists ( ))
//                            document2.delete ( );
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                e.printStackTrace();
            }
        });
    }


    private RequestBody addDocuments(Boolean isPassword, Activity activity) {
        RequestBody requestBody;
        document1 = new File(((MyApplication) activity.getApplicationContext()).getUrlDocFront());
        if (isPassword) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("contract_id", ((MyApplication) activity.getApplicationContext()).getContractId())
                    .addFormDataPart("user_id",  ((MyApplication) activity.getApplicationContext()).getUser_id())
                    .addFormDataPart("country", ((MyApplication) activity.getApplicationContext()).getSelectedCountyCo2().toUpperCase())
                    .addFormDataPart("file_type", "passport")
                    .addFormDataPart("document1", "document1.jpg", RequestBody.create(MediaType.parse("image/jpg"), document1))
                    .build();
        } else {
            document2 = new File(((MyApplication) activity.getApplicationContext()).getUrlDocBack());
            String fileType = "driving-license";
            if (((MyApplication) activity.getApplicationContext()).getSelectedDocument() == MyApplication.typeDocument.DNI) {
                fileType = "national-id";
            }
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("contract_id", ((MyApplication) activity.getApplicationContext()).getContractId())
                    .addFormDataPart("user_id", ((MyApplication) activity.getApplicationContext()).getUser_id())
                    .addFormDataPart("country", ((MyApplication) activity.getApplicationContext()).getSelectedCountyCo2().toUpperCase())
                    .addFormDataPart("file_type", fileType)
                    .addFormDataPart("document1",
                            "document1.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document1))
                    .addFormDataPart("document2",
                            "document2.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document2))
                    .build();
        }
        return requestBody;
    }

    private RequestBody addDocumentsAndVideo(Boolean isPassword, Activity activity) {
        RequestBody requestBody;
        video = new File(((MyApplication) activity.getApplicationContext()).getUrlVideo());
        document1 = new File(((MyApplication) activity.getApplicationContext()).getUrlDocFront());
        if (isPassword) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("contract_id", ((MyApplication) activity.getApplicationContext()).getContractId())
                    .addFormDataPart("user_id", ((MyApplication) activity.getApplicationContext()).getUser_id())
                    .addFormDataPart("country", ((MyApplication) activity.getApplicationContext()).getSelectedCountyCo2().toUpperCase())
                    .addFormDataPart("file_type", "passport")
                    .addFormDataPart("video",
                            "video.mp4",
                            RequestBody.create(MediaType.parse("video/mp4"), video))
                    .addFormDataPart("document1",
                            "document1.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document1))
                    .build();
        } else {

            document2 = new File(((MyApplication) activity.getApplicationContext()).getUrlDocBack());
            String fileType = "driving-license";
            if (((MyApplication) activity.getApplicationContext()).getSelectedDocument() == MyApplication.typeDocument.DNI) {
                fileType = "national-id";
            }
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("contract_id", ((MyApplication) activity.getApplicationContext()).getContractId())
                    .addFormDataPart("user_id", ((MyApplication) activity.getApplicationContext()).getUser_id())
                    .addFormDataPart("country", ((MyApplication) activity.getApplicationContext()).getSelectedCountyCo2().toUpperCase())
                    .addFormDataPart("file_type", fileType)
                    .addFormDataPart("video",
                            "video.mp4",
                            RequestBody.create(MediaType.parse("video/mp4"), video))
                    .addFormDataPart("document1",
                            "document1.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document1))
                    .addFormDataPart("document2",
                            "document2.jpg",
                            RequestBody.create(MediaType.parse("image/jpg"), document2))
                    .build();
        }
        return requestBody;
    }

    public void getDataAutentication(String urlGetResponse, final Activity activity, final AsynchronousTask asynchronousTask) {
        AsyncTask.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .readTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS)
                        .writeTimeout(activity.getResources().getInteger(R.integer.timeOut), TimeUnit.SECONDS).build();

                Request request = new Request.Builder()
                        .url(urlGetResponse)
                        .header("Authorization", "Bearer " + ((MyApplication) activity.getApplicationContext()).getAccess_token())
                        .get()
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                        asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = new JSONObject(jsonData);

                            if (Jobject.has("apimsg")) {
                                asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.PENDING, Jobject.getString("apimsg")), USERRESPONSE);
                            } else if (Jobject.has("verification") &&
                                    Jobject.has("userAgent") &&
                                    Jobject.has("registry") &&
                                    Jobject.has("media")) {
                                JSONObject JobjectV = new JSONObject(Jobject.getString("verification"));
                                JSONObject JobjectUA = new JSONObject(Jobject.getString("userAgent"));
                                JSONObject JobjectR = new JSONObject(Jobject.getString("registry"));
                                JSONObject JobjectM = new JSONObject(Jobject.getString("media"));

                                if (JobjectV.has("verification_status")) {

                                    if (JobjectV.getString("verification_status").equals("La verificacion tuvo un error")) {
                                        asynchronousTask.onReceiveResultsTransaction(new ResponseIV(ResponseIV.ERROR, JobjectV.getString("verification_status")), USERRESPONSE);
                                    } else {
                                        boolean face_match = false,
                                                template = false,
                                                alteration = false,
                                                watch_list = false;
                                        if (JobjectV.getString("verification_status").equals("completed")) {
                                            if (!JobjectV.getString("face_match").equals("true"))
                                                face_match = true;
                                            if (!JobjectV.getString("template").equals("true"))
                                                template = true;
                                            if (!JobjectV.getString("alteration").equals("true"))
                                                alteration = true;
                                            if (!JobjectV.getString("watch_list").equals("true"))
                                                watch_list = true;
                                            JSONObject JComplyAdvantage = new JSONObject(Jobject.getString("comply_advantage"));

                                            ResponseIV responseIV = new ResponseIV(
                                                    Jobject.getString("id"),
                                                    Jobject.getString("created_at"),
                                                    Jobject.getString("company"),
                                                    Jobject.getString("fullname"),
                                                    Jobject.getString("birth"),
                                                    Jobject.getString("document_type"),
                                                    Jobject.getString("document_number"),
                                                    face_match,
                                                    template,
                                                    alteration,
                                                    watch_list,
                                                    JComplyAdvantage.getString("comply_advantage_result"),
                                                    JComplyAdvantage.getString("comply_advantage_url"),
                                                    JobjectV.getString("verification_status"),
                                                    (JobjectUA.has("device_model") ? JobjectUA.getString("device_model") : ""),
                                                    (JobjectUA.has("os_version") ? JobjectUA.getString("os_version") : ""),
                                                    (JobjectUA.has("browser_major") ? JobjectUA.getString("browser_major") : ""),
                                                    (JobjectUA.has("browser_version") ? JobjectUA.getString("browser_version") : ""),
                                                    (JobjectUA.has("ua") ? JobjectUA.getString("ua") : ""),
                                                    (JobjectUA.has("device_type") ? JobjectUA.getString("device_type") : ""),
                                                    (JobjectUA.has("device_vendor") ? JobjectUA.getString("device_vendor") : ""),
                                                    (JobjectUA.has("os_name") ? JobjectUA.getString("os_name") : ""),
                                                    (JobjectUA.has("browser_name") ? JobjectUA.getString("browser_name") : ""),
                                                    (JobjectR.has("issuePlace") ? JobjectR.getString("issuePlace") : ""),
                                                    (JobjectR.has("emissionDate") ? JobjectR.getString("emissionDate") : ""),
                                                    (JobjectR.has("ageRange") ? JobjectR.getString("ageRange") : ""),
                                                    (JobjectR.has("savingAccountsCount") ? JobjectR.getInt("savingAccountsCount") : 0),
                                                    (JobjectR.has("financialIndustryDebtsCount") ? JobjectR.getInt("financialIndustryDebtsCount") : 0),
                                                    (JobjectR.has("solidarityIndustryDebtsCount") ? JobjectR.getInt("solidarityIndustryDebtsCount") : 0),
                                                    (JobjectR.has("serviceIndustryDebtsCount") ? JobjectR.getInt("serviceIndustryDebtsCount") : 0),
                                                    (JobjectR.has("commercialIndustryDebtsCount") ? JobjectR.getInt("commercialIndustryDebtsCount") : 0),
                                                    (Jobject.has("ip") ? Jobject.getString("ip") : ""),
                                                    (JobjectM.has("frontImgUrl") ? JobjectM.getString("frontImgUrl") : ""),
                                                    (JobjectM.has("backImgUrl") ? JobjectM.getString("backImgUrl") : ""),
                                                    (JobjectM.has("selfiImageUrl") ? JobjectM.getString("selfiImageUrl") : ""),
                                                    "verification complete",
                                                    ResponseIV.SUCCES
                                            );
                                            asynchronousTask.onReceiveResultsTransaction(responseIV, USERRESPONSE);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                        }
                    }
                });


            } catch (Exception e) {
                asynchronousTask.onErrorTransaction(e.getLocalizedMessage());
                e.printStackTrace();
            }
        });
    }

}
