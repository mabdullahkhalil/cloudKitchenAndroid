package com.something.mabdullahk.cloudkitchen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private Button login;
    private Button fb;
    private Button signUp;
    private EditText username;
    private EditText password;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AppEventsLogger.activateApp(((Startactivity)getActivity()).getApplication());
        callbackManager = CallbackManager.Factory.create();

        View view  = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        login = (Button) view.findViewById(R.id.login);
        fb = (Button) view.findViewById(R.id.fb);
        signUp = (Button) view.findViewById(R.id.signup);
        username = (EditText) view.findViewById(R.id.username);
        password= (EditText) view.findViewById(R.id.password);

        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        preferences = getActivity().getSharedPreferences("Cloud Kitchen", MODE_PRIVATE);
        editor = preferences.edit();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Startactivity)getActivity()).setViewPager(1);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> params = new HashMap<>();
                Map<String, String> headers = new HashMap<>();

                params.put("email",username.getText().toString().trim());
                params.put("password",password.getText().toString().trim());

                HTTPrequest.placeRequest("http://backend-fooddelivery.herokuapp.com/api/signin", "Post", params, headers, new HTTPrequest.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("Response Gotten", result);
                        final String token;
                        try {
                            JSONObject response = new JSONObject(result);


                            token= response.getString("token");

                            final String message= response.getString("message");
                            Log.d("Response Gotten", message);

                            if(!message.equals("")){
                                // changed here
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                Log.d("Ressssssssss",result);
                                try {
                                    JSONObject response1 = new JSONObject(result);
                                    intent.putExtra("token",response1.getString("token"));
                                    editor.putString(encrypt("token"),encrypt(response1.getString("token")));
                                    editor.commit();
                                    startActivity(intent);
                                    getActivity().finish();

                                } catch (JSONException e1){

                                }
                            }
                            if (message.equals("your phone is not verified yet")){

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                // Get the layout inflater
                                final LayoutInflater inflater = getActivity().getLayoutInflater();
                                View view1 = inflater.inflate(R.layout.verification_code, null);

                                // Inflate and set the layout for the dialog
                                // Pass null as the parent view because its going in the dialog layout
                                final EditText code = (EditText) view1.findViewById(R.id.verificationcode);

                                builder.setView(view1)
                                        .setMessage("Enter Code sent to your registered mobile number.")
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

                                            HTTPrequest.placeRequest("http://backend-fooddelivery.herokuapp.com/api/verify_phone", "Post", params, headers, new HTTPrequest.VolleyCallback() {
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
                                            }, (Startactivity)getActivity());

                                            // sign in the user ...
                                        }
                                    }
                                });
                            } else {





                            }

                        } catch (JSONException e){
                            // changed here
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            Log.d("Ressssssssss",result);
                            try {
                                JSONObject response1 = new JSONObject(result);
                                intent.putExtra("token",response1.getString("token"));
                                editor.putString(encrypt("token"),encrypt(response1.getString("token")));
                                editor.commit();
                                startActivity(intent);
                                getActivity().finish();

                            } catch (JSONException e1){

                            }

                        }
                    }

                    @Override
                    public void onFaliure(String faliure) {

                    }
                }, (Startactivity)getActivity());
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

//            L("HELLOE", "hekwjeijfihgi")
            @Override
            public void onSuccess(LoginResult loginResult) {

                Set<String> deniedPermissions = loginResult.getRecentlyGrantedPermissions();

                System.out.println("showingggggg"+deniedPermissions);

                if (!deniedPermissions.contains("email")) {
                    System.out.println("showingggggg nothinggg");

                    LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email","public_profile"));

                }

                System.out.println(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                System.out.println(loginResult.getAccessToken().getToken());


                RequestQueue queue = Volley.newRequestQueue(getActivity());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://backend-fooddelivery.herokuapp.com/api/auth/facebook/token"+"?access_token="+loginResult.getAccessToken().getToken(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("reponse cameeeeee.....");
                                System.out.println("Response is: "+ response);

                                try {
                                    JSONObject parentObject = new JSONObject(response);

                                    System.out.println(parentObject);
                                    final String token;
                                    final String token1;

                                    try {
                                        JSONObject phone = parentObject.getJSONObject("phoneDetails");
                                        token1 = parentObject.getString("authtoken");
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.putExtra("token",token1);
                                        getActivity().finish();
                                        startActivity(intent);

                                    } catch (JSONException e) {
                                        System.out.println("no details found");
                                        token = parentObject.getString("authtoken");
                                        Intent intent = new Intent(getActivity(), phoneAdding.class);
                                        intent.putExtra("token",token);
                                        getActivity().finish();
                                        startActivity(intent);



                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        info.setText("That didn't work!");
                        System.out.println("that didnt work");
                    }
                });

                queue.add(stringRequest);
            }

            @Override
            public void onCancel() {
                System.out.println("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                System.out.println("Login attempt failed."+e);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }

}
