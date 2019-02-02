package com.something.mabdullahk.cloudkitchen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements foodCartAdapter.AdapterCallback {

    String token;
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    RecyclerView cartRecyclerView;
    List<foodCard> mFoodList;
    foodCard foodcards;
    Calendar calendar;
    LinearLayout weekOne;
    private Button btn_unfocus;
    Button[] days = new Button[6];
    JSONObject jsonObj = new JSONObject();
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TabLayout tabLayout;
    MenuItem item;
    Integer count;
    List<String> dates_of_the_week;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView cartitems;



    public interface showmealscallback{
        void onSuccess(List<foodCard> result);
        void onFaliure(String faliure);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dates_of_the_week = new ArrayList<>();

        tabLayout = (TabLayout) findViewById(R.id.calendar);

        preferences = getSharedPreferences("Cloud Kitchen", MODE_PRIVATE);
        editor = preferences.edit();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.cart);


        makecalendar();
        count=0;

        if (preferences.contains(encrypt("carttoken"))){
            token = decrypt(preferences.getString(encrypt("carttoken"), null));
            System.out.println("the token is: "+token);
            editor.remove(encrypt("carttoken"));
            editor.commit();
        }


        if (token==null){
            Intent intent = getIntent();
            token = intent.getStringExtra("token");
            Log.d("TOEEKENEE",token);
        }
        System.out.println("the token is: "+token);



        mRecyclerView = findViewById(R.id.recyclerview);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);


        GridLayoutManager foodMenuGrid = new GridLayoutManager(MainActivity.this, 3);
        GridLayoutManager cartMenuGrid = new GridLayoutManager(MainActivity.this, 2);

        mRecyclerView.setLayoutManager(foodMenuGrid);
        cartRecyclerView.setLayoutManager(cartMenuGrid);
        cartitems = (TextView) findViewById(R.id.cartitemstext);

        cartitems.setVisibility(View.INVISIBLE);

        mFoodList = new ArrayList<>();



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                newlist(Integer.toString(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        showmeals(token,new showmealscallback() {
            @Override
            public void onSuccess(List<foodCard> result) {
                System.out.println("setting up the adapter"+ result.size());
                foodCartAdapter myAdapter = new foodCartAdapter(MainActivity.this,result,MainActivity.this);
                mRecyclerView.setAdapter(myAdapter);
//                mRecyclerVie.setAdapter(myAdapter);
                System.out.println("set ed  up the adapter");
                mFoodList = result;
            }

            @Override
            public void onFaliure(String faliure) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart, menu);
        MenuItem item1 = menu.findItem(R.id.carticon);
        item1.setIcon(buildCounterDrawable(count, R.drawable.cart));
        item = item1;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        System.out.println("pressed"+ item);

        switch(item.getItemId()) {
            case R.id.carticon:
                Intent favIntent = new Intent(this, Cartcheckout.class);
                favIntent.putExtra("FoodList", (Serializable) mFoodList);
                favIntent.putExtra("cartItems",jsonObj.toString());
                favIntent.putExtra("dates",(ArrayList) dates_of_the_week    );
                favIntent.putExtra("token",token);
                startActivity(favIntent);
                break;
                // another startActivity, this is for item with id "menu_item2"
            default:
                return super.onOptionsItemSelected(item);
        }
            return true;
    }

    private void showmeals(String token, final showmealscallback mealscallback){
        final List<foodCard> mFoodList = new ArrayList<>();
        final foodCard[] foodcards = new foodCard[1];
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();

        headers.put("token",token);
        HTTPrequest.placeRequest("http://backend-fooddelivery.herokuapp.com/api/showMeal", "Get", params, headers, new HTTPrequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Ressss", result);
                try {
                    JSONObject meals = new JSONObject(result);
                    JSONArray foods = meals.getJSONArray("meals");
                    for (int i =0;i<foods.length();i++){


                        JSONObject foodthings = foods.getJSONObject(i);
                        System.out.println("foodthings" + foodthings);
                        String name = foodthings.getString("name");
                        String id = foodthings.getString("_id");
                        String location = foodthings.getString("location");
                        String mealImageUrl = foodthings.getString("mealImageUrl");
                        int price = foodthings.getInt("price");



                        JSONArray result_options = foodthings.getJSONArray("options");
                        JSONArray result_days = foodthings.getJSONArray("days");

                        List<String> days = new ArrayList<String>();
                        for(int j = 0; j < result_days.length(); j++){
                            days.add(result_days.getString(j));
                        }


                        List<String> options = new ArrayList<String>();
                        for(int j = 0; j < result_options.length(); j++){
                            options.add(result_options.getString(j));
                        }

                        foodcards[0] = new foodCard(name,options,days,price,mealImageUrl,id,location,"food");
                        mFoodList.add(new foodCard(name,options,days,price,mealImageUrl,id,location,"food"));

                        System.out.println("food added");
                    }

                    System.out.println("fodddddlosy"+mFoodList);
                    mealscallback.onSuccess(mFoodList);

                } catch (JSONException e){
                    System.out.println("there is an error"+e);
                }
            }

            @Override
            public void onFaliure(String faliure) {

            }
        }, MainActivity.this);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void makecalendar(){


        SimpleDateFormat df = new SimpleDateFormat("EEEE, dd MMMM YYYY");

        calendar = Calendar.getInstance();
        int currentDateDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentDateMonth  = calendar.get(Calendar.MONTH);
        int currentDateYear =
                calendar.get(Calendar.YEAR);
        int hour_of_the_day = calendar.get(Calendar.HOUR_OF_DAY);
        System.out.print(hour_of_the_day);

        int daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfCurrentMonth =
                calendar.get(Calendar.DAY_OF_WEEK);

        System.out.println("calendar" +"   "+ currentDateDay +"   "+ currentDateMonth +"   "+ currentDateYear+"   " + daysInCurrentMonth);

        Calendar date = Calendar.getInstance();


        if(hour_of_the_day > 11){
            currentDateDay++;
            firstDayOfCurrentMonth++;
            date.add(Calendar.DATE,1);
        }





        for (int i =0; i< 6; i++){
            try {
                jsonObj.put(Integer.toString(i),new JSONArray());
            } catch (JSONException e){
                System.out.println("in make calendar err");
            }

            System.out.println("check "+firstDayOfCurrentMonth);
                if (currentDateDay == daysInCurrentMonth+1){
                    currentDateDay = 1;
                    date.add(Calendar.DATE,1);
                }
                if (firstDayOfCurrentMonth == 8){
                    firstDayOfCurrentMonth=1;
                }
                switch (firstDayOfCurrentMonth){
                    case Calendar.SUNDAY:
                        System.out.println("SUNDAY RECORDED");
                        currentDateDay++;
                        date.add(Calendar.DATE,1);

//                        i--;
                    case Calendar.MONDAY:
                        tabLayout.addTab(tabLayout.newTab().setText("MON"+" \n"+currentDateDay));

                      break;
                    case Calendar.TUESDAY:
                        tabLayout.addTab(tabLayout.newTab().setText("TUE \n"+currentDateDay));
                        break;
                    case Calendar.WEDNESDAY:
                        tabLayout.addTab(tabLayout.newTab().setText("WED \n"+currentDateDay));
                        break;
                    case Calendar.THURSDAY:
                        tabLayout.addTab(tabLayout.newTab().setText("THU \n"+currentDateDay));

                        break;
                    case Calendar.FRIDAY:
                        tabLayout.addTab(tabLayout.newTab().setText("FRI \n"+currentDateDay));
                        break;
                    case Calendar.SATURDAY:
                        tabLayout.addTab(tabLayout.newTab().setText("SAT \n"+currentDateDay));
                        break;
                }
            String formattedDate = df.format(date.getTime());
            System.out.println(formattedDate);
            dates_of_the_week.add(formattedDate);
            firstDayOfCurrentMonth++;
            currentDateDay++;
            date.add(Calendar.DATE,1);


        }

        System.out.println("DAYSSSSSSS "+days);

    }


    @Override
    public void onItemClicked(String fooddata) {
        cartitems.setVisibility(View.VISIBLE);
        try {
            JSONArray foods_selected = jsonObj.getJSONArray(Integer.toString(tabLayout.getSelectedTabPosition()));

            if (foods_selected.length()==5){
                Toast.makeText(this,"Limit Exceeded for today Items",Toast.LENGTH_LONG).show();
                return;
            }
            count++;
            item.setIcon(buildCounterDrawable(count, R.drawable.cart));

            foods_selected.put(fooddata);

            foodCard food = new foodCard();
            List<foodCard> new_card = new ArrayList<>();
//            System.out.println("the food list is"+mFoodList.toString());
//            for (foodCard c : mFoodList) {
//                System.out.println("1st");
//                System.out.println(fooddata);
//                System.out.println(c.getId());
//                if (fooddata.equals(c.getId())) {
//                    System.out.println(c.getType());
//                    food = new foodCard(c);
//                    new_card.add(food);
//                    System.out.println(food.getType());
//                    System.out.println(c.getType());
//                    break;
//                }
//            }



            for (int i=0; i< foods_selected.length(); i++){

                fooddata = foods_selected.getString(i);
                System.out.println(fooddata);
                for (foodCard c : mFoodList) {
                    System.out.println("2nd");
                    System.out.println(fooddata);
                    System.out.println(c.getId());
                    if (fooddata.equals(c.getId())) {
                        System.out.println(c.getType());
                        food = new foodCard(c);
                        new_card.add(food);
                        System.out.println(food.getType());
                        System.out.println(c.getType());
                        break;
                    }
                }
            }

            System.out.println(new_card);

//            System.out.println("check364" + foods_selected);



//            System.out.println("check371");
            System.out.println(foods_selected);


            jsonObj.put(Integer.toString(tabLayout.getSelectedTabPosition()),foods_selected);

//            System.out.println("OBEJHYGSDFB" + jsonObj);

            foodCartAdapter myAdapter = new foodCartAdapter(MainActivity.this,new_card,MainActivity.this);
            cartRecyclerView.setAdapter(myAdapter);
            System.out.println(jsonObj);
        } catch (JSONException e){
            System.out.println("json error in onItemCLicked" + e);
        }
    }

    public void newlist(String position){
        try {
            String fooddata;
//            foodCard food = new foodCard();
            List<foodCard> new_card = new ArrayList<>();
            JSONArray foods_selected = jsonObj.getJSONArray(position);
            for (int i=0; i< foods_selected.length(); i++){
                fooddata = foods_selected.getString(i);
                for (foodCard c : mFoodList) {
                    System.out.println(fooddata);
                    System.out.println(c.getId());
                    if (fooddata.equals(c.getId())) {
                        System.out.println(c.getType());
                        foodCard food = new foodCard(c);
                        new_card.add(food);
                        System.out.println(food.getType());
                        System.out.println(c.getType());
                        break;
                    }
                }
            }


//            System.out.println("the size of the card 1 "+new_card.size());


//            System.out.println("the size of the card 2 "+new_card.size());

            foodCartAdapter myAdapter = new foodCartAdapter(MainActivity.this,new_card,MainActivity.this);
            cartRecyclerView.setAdapter(myAdapter);


        } catch (JSONException e){
            System.out.println("error in btn1");
        }
    }

    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//        savedInstanceState.putString("token",token);
//        System.out.println("the instance is saved");
//        // etc.
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
////        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        System.out.println("the instance is restored");
//
//        token = savedInstanceState.getString("token");
//    }
//    @Override
//    public void onResume(){
//        super.onResume();
//        System.out.println("This is onResume method <<<<<<<<<");
//
//        if(token!=null){
//            token = token; //important, or it will run only once.
//            // Do your code
//        }
//    }



    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }
}




