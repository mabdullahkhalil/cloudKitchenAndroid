package com.something.mabdullahk.cloudkitchen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cartcheckout extends AppCompatActivity {

    String token;
    List<foodCard> mFoodList;
    List<String> dates;
    String cartItems;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Button continuebtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartcheckout);

        getSupportActionBar().setTitle("Shopping Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        token = intent.getStringExtra("token");
        dates  =intent.getStringArrayListExtra("dates");
        mFoodList = (List<foodCard>) intent.getSerializableExtra("FoodList");
        cartItems = intent.getStringExtra("cartItems");
        continuebtn = (Button) findViewById(R.id.btncontinue);

        Boolean ordercheker = true;

        System.out.println("cart items "+cartItems);
        System.out.println(dates+"DTAES");
        View scollview = findViewById(R.id.cartlinear);

        try{

            JSONObject jsonObj = new JSONObject(cartItems);
            for(int j =0; j<6;j++){

                JSONArray foods_selected = jsonObj.getJSONArray(Integer.toString(j));
                int price =0;
                int numberofItems=0;
                for (int i=0; i< foods_selected.length(); i++){

                    String fooddata = foods_selected.getString(i);
                    System.out.println(fooddata);
                    for (foodCard c : mFoodList) {
                        System.out.println("2nd");
                        System.out.println(fooddata);
                        System.out.println(c.getId());
                        if (fooddata.equals(c.getId())) {
                            price = price + c.getPrice();
                            numberofItems++;
                            break;
                        }
                    }
                }

                if(numberofItems!=0) {
                    ordercheker=false;
                    showdata(Integer.toString(price), Integer.toString(numberofItems), dates.get(j), scollview);
                }
            }

        }catch (JSONException e){

        }

        final Boolean finalOrdercheker = ordercheker;
        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalOrdercheker){
                    Toast.makeText(Cartcheckout.this, "YOU have nothing in your cart yet. PLease Add items to Contiue.",Toast.LENGTH_LONG).show();
                } else {

                    final LayoutInflater inflater = getLayoutInflater();

                    View view1 = inflater.inflate(R.layout.address, null);

                    ListView listPlaces = (ListView) view1.findViewById(R.id.listLocation);

                    String[] places = new String[]{
                            "DHA",
                            "Model Town"
                    };

                    // Create a List from String Array elements
                    final List<String> places_list = new ArrayList<String>(Arrays.asList(places));


                    listPlaces.setAdapter(new ArrayAdapter(Cartcheckout.this, android.R.layout.simple_list_item_checked, places_list));


                    AlertDialog.Builder alert = new AlertDialog.Builder(Cartcheckout.this);
                    alert.setTitle("Put your address:");
                    alert.setIcon(R.drawable.cloud_kitchen_logo);

                    alert.setView(view1);

                    final String[] items = new String[]{"DHA", "Model Town"};


                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(Cartcheckout.this, "YOUR ORDER HAS BEEN PLACED", Toast.LENGTH_LONG).show();

                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();
                }

            }
        });
    }


    private void showdata(String price, String items, String Date, View scollview){
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        LinearLayout layout1 = horizontaldata("Order On: ",Date);
        LinearLayout layout2 = horizontaldata("Total Items: ",items);
        LinearLayout layout3 = horizontaldata("Total Price: ","Rs: "+price);

        layout.addView(layout1);
        layout.addView(layout2);
        layout.addView(layout3);

        layout.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
        ((LinearLayout) scollview).addView(layout);
    }

    private LinearLayout horizontaldata(String mention, String item){
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);

        TextView day = new TextView(this);
        day.setText(mention);
        day.setTypeface(day.getTypeface(), Typeface.BOLD);

        TextView day1 = new TextView(this);
        day1.setText(item);

        day.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        day1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(day);
        layout.addView(day1);

        return layout;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("you pressed"+item.getItemId());
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }
}
