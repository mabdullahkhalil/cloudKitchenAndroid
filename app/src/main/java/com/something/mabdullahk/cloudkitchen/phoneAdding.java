package com.something.mabdullahk.cloudkitchen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class phoneAdding extends AppCompatActivity {

    Button phoneadd;
    EditText number;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_adding);


        phoneadd= (Button) findViewById(R.id.phoneadd);
        number = (EditText) findViewById(R.id.number);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");


        phoneadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneaddFunc();
            }
        });

    }

    private void phoneaddFunc(){
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        String num = number.getText().toString().trim();

        headers.put("token",token);

        if (!num.equals("")){
            params.put("phoneNumber",num);
            HTTPrequest.placeRequest("http://backend-fooddelivery.herokuapp.com/api/facebook_phone_adding", "Post", params, headers, new HTTPrequest.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(phoneAdding.this);
                    // Get the layout inflater
                    final LayoutInflater inflater = getLayoutInflater();
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
                                        Toast.makeText(phoneAdding.this, "Code verified", Toast.LENGTH_SHORT).show();
                                        System.out.println(result);
                                        try {
                                            JSONObject response = new JSONObject(result);

//                                            token = response.getString("token");

                                        Intent intent = new Intent(phoneAdding.this, MainActivity.class);
                                        intent.putExtra("token",token);
                                        finish();
                                        startActivity(intent);

                                        } catch (JSONException e){

                                        }
//                                        token1 = parentObject.getString("authtoken");
//                                        Intent intent = new Intent(phoneAdding.this, MainActivity.class);
//                                        intent.putExtra("token",token1);
//                                        finish();
//                                        startActivity(intent);

                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFaliure(String faliure) {
                                        Log.d("Faliur",faliure);
                                        code.setError("Wrong Verification Code");
                                        dialog.show();

                                    }
                                }, phoneAdding.this);

                                // sign in the user ...
                            }
                        }
                    });
                }

                @Override
                public void onFaliure(String faliure) {

                }
            },this);
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Complete Verification first",Toast.LENGTH_SHORT).show();
    }

}
