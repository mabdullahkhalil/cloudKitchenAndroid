package com.something.mabdullahk.cloudkitchen;

import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/**
 * Created by mabdullahk on 23/09/2018.
 */

public class HTTPrequest {

    public interface VolleyCallback{
        void onSuccess(String result);
        void onFaliure(String faliure);
    }

    public static void placeRequest(String url, String method, final Map<String, String> params, final Map<String, String> headers, final VolleyCallback callback, Activity name){

        switch (method){
            case "Post":
                RequestQueue queue = Volley.newRequestQueue(name);

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

            case "Get":
                RequestQueue queue1 = Volley.newRequestQueue(name);

// Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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

// Add the request to the RequestQueue.
                queue1.add(stringRequest);

                break;
        }

    }
}
