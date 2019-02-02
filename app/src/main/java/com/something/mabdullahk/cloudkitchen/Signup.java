package com.something.mabdullahk.cloudkitchen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class Signup extends Fragment {

    private static final int PHONE_NUMBER_HINT = 100;
    private static final int EMAIL_HINT = 1000;
    private final int PERMISSION_REQ_CODE = 200;
    private EditText username;
    private EditText password;
    private EditText cnfrmpassword;
    private EditText email;
    private EditText phone;
    private Button signup;
    private Task<Void> task;
    private ProgressDialog progress;
    final Context context = ((Startactivity)getActivity());
    AlertDialog alertDialog;




    public interface VolleyCallback{
        void onSuccess(String result);
        void onFaliure(String faliure);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view  = inflater.inflate(R.layout.fragment_signup, container, false);

        username = (EditText) view.findViewById(R.id.username) ;
        password = (EditText) view.findViewById(R.id.password) ;
        cnfrmpassword = (EditText) view.findViewById(R.id.cnfrmpassword) ;
        email = (EditText) view.findViewById(R.id.email) ;
        phone = (EditText) view.findViewById(R.id.phone) ;
        signup = (Button) view.findViewById(R.id.signup);
        progress = new ProgressDialog(getActivity());


//        email.setFocusable(false);
//        phone.setFocusable(false);





        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                progress.setMessage("Signing Up...");
                progress.show();
                if (password.getText().toString().trim().equals(cnfrmpassword.getText().toString().trim())){

                }
                String url = "http://backend-fooddelivery.herokuapp.com/api/signup";
                Map<String,String> params= new HashMap<>();
                Map<String,String> headers= new HashMap<>();

                params.put("username", username.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                params.put("phoneNumber", phone.getText().toString().trim());

                httpRequest(url, "Post", params, headers, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        try{
                            progress.dismiss();

                            JSONObject signUpresponse = new JSONObject(result);
                            final String token= signUpresponse.getString("token");

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            // Get the layout inflater
                            final LayoutInflater inflater = getActivity().getLayoutInflater();
                            View view1 = inflater.inflate(R.layout.verification_code, null);

                            // Inflate and set the layout for the dialog
                            // Pass null as the parent view because its going in the dialog layout
                            final EditText code = (EditText) view1.findViewById(R.id.verificationcode);

                            builder.setView(view1)
                                    // Add action buttons
                                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(final DialogInterface dialog, int id) {

                                        }
                                    });

                            final AlertDialog dialog = builder.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();

                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Log.d("Code", code.getText().toString());
                                    Log.d("tok", token);

                                    if (!code.getText().toString().trim().equals("")) {
                                        Map<String, String> params = new HashMap<>();
                                        Map<String, String> headers = new HashMap<>();

                                        Log.d("Code", code.getText().toString());
                                        Log.d("tok", token);
                                        params.put("verificationCode", code.getText().toString().trim());

                                        headers.put("token",token);

                                        httpRequest("http://backend-fooddelivery.herokuapp.com/api/verify_phone", "Post", params, headers, new VolleyCallback() {
                                            @Override
                                            public void onSuccess(String result) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Code verified", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onFaliure(String faliure) {
                                                Log.d("Faliur",faliure);
                                                code.setError("Wrong Verification Code");
                                                dialog.show();

                                            }
                                        });

                                        // sign in the user ...
                                    }
                                }
                            });

                        }catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onFaliure(String faliure) {

                    }
                });



            }

        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    hintRequest(2);
                }
            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    hintRequest(1);
                }
            }
        });

        return view;
    }


    private void hintRequest(int hint){
        switch (hint) {
            case 1:
                if (phone.getText().toString().matches("")){
                    HintRequest hintRequest = new HintRequest.Builder()
                            .setPhoneNumberIdentifierSupported(true)
                            .build();
                    try {
                        final GoogleApiClient googleApiClient =
                                new GoogleApiClient.Builder((Startactivity) getActivity()).addApi(Auth.CREDENTIALS_API).build();

                        final PendingIntent pendingIntent =
                                Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
                        getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                                PHONE_NUMBER_HINT, null, 0, 0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            case 2:
                if (email.getText().toString().matches("")) {

                    HintRequest hintRequest1 = new HintRequest.Builder()
                            .setEmailAddressIdentifierSupported(true)
                            .build();
                    try {
                        final GoogleApiClient googleApiClient =
                                new GoogleApiClient.Builder((Startactivity) getActivity()).addApi(Auth.CREDENTIALS_API).build();

                        final PendingIntent pendingIntent =
                                Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest1);
                        getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                                EMAIL_HINT, null, 0, 0, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else{
//                    email.setKeyListener((KeyListener) email.getTag());
                    email.isTextSelectable();

                }
                return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("after result", data.toString());
        Log.d("after result resultcoed", Integer.toString(resultCode));
        Log.d("after result requestco", Integer.toString(requestCode));

        if (resultCode == RESULT_OK) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            Log.d("after result",credential.getId());
            switch (requestCode){
                case EMAIL_HINT:
                    email.setText(credential.getId());
                    return;
                case PHONE_NUMBER_HINT:
                    phone.setText((credential.getId()));
                    return;
            }
        }
    }

    private void httpRequest(String url, String method, final Map<String, String> params, final Map<String, String> headers,final VolleyCallback callback){

        switch (method){
            case "Post":
                RequestQueue queue = Volley.newRequestQueue(getActivity());

                StringRequest jsonObjReq = new StringRequest(Request.Method.POST,url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.e("Response",response.toString());
                                Log.e("Response",response);
                                callback.onSuccess(response.toString());

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        VolleyLog.d("Error: " + error);
                        callback.onFaliure(error.toString());

                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return headers;
                    }
                    @Override
                    protected Map<String, String> getParams() {
                        return params;
                    }
                };
                queue.add(jsonObjReq);
                break;
        }

    }




}
