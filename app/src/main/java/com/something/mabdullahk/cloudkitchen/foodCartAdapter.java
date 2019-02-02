package com.something.mabdullahk.cloudkitchen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mabdullahk on 27/09/2018.
 */



public class foodCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public interface AdapterCallback{
        void onItemClicked(String fooddata);
    }

    private Activity mContext;
    private List<foodCard> mFoodList;
    AdapterCallback adapterCallback;
    final int ITEM_CARD =0;
    final int ITEM_FOOD =1;
    final int ITEM_TEXT =2;


    foodCartAdapter(Activity mContext, List mFoodList,AdapterCallback callback){
        this.mContext = mContext;
        this.mFoodList = mFoodList;
        this.adapterCallback = callback;
    }





    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("view type::::"+ viewType);

        if (viewType == ITEM_CARD){
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartcard, parent, false);
            return new cartViewHolder(mView);
        }
        if (viewType == ITEM_TEXT){
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_textview, parent, false);
            return new foodTextView(mView);
        }
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodcard, parent, false);
            return new FoodCardViewHolder(mView);


    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final int itemType = getItemViewType(position);


        if (itemType == ITEM_CARD){

            System.out.println(mFoodList.get(position).getPrice()+ "is the price");
            ((cartViewHolder)holder).name.setText(mFoodList.get(position).getName());
            ((cartViewHolder)holder).price.setText("Rs: "+Integer.toString(mFoodList.get(position).getPrice()));
            List<String> opts = mFoodList.get(position).getOptions();
            String options = new String();
            System.out.println("your opti"+opts.size());
            for (int i= 0;i< opts.size();i++){
                if(i==0){
                    options = opts.get(0);
                }else {
                    options = options + "," + opts.get(i);

                }}

            ((cartViewHolder)holder).addon.setText(options);


        }
        if (itemType == ITEM_TEXT){

        }
        if (itemType == ITEM_FOOD){

            ((FoodCardViewHolder)holder).mTitle.setText(mFoodList.get(position).getName());
            ((FoodCardViewHolder)holder).price.setText("Rs: "+Integer.toString(mFoodList.get(position).getPrice()));
            ((FoodCardViewHolder)holder).addbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(mContext,mFoodList.get(((FoodCardViewHolder)holder).getAdapterPosition()).getName(),Toast.LENGTH_SHORT).show();


                    AlertDialog.Builder  options_menu= new AlertDialog.Builder(mContext);
                    final String[] items = new String[]{"Roti","Kheer","Cold Drink 0.5 litre"};
                    final Boolean[] items_bool = new Boolean[]{false,false,false};
                    List<String> items_final = Arrays.asList(items);
                    final List<String> selected_items= new ArrayList<>();

                    options_menu.setTitle("Add-Ons");
                    options_menu.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    items_bool[i] = b;
                                }
                            });
                    options_menu.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for(int j =0; j<items_bool.length;j++){
                                if (items_bool[j]){
                                    selected_items.add(items[j]);
                                }
                            }
                            mFoodList.get(position).setOptions(selected_items);
                            adapterCallback.onItemClicked(mFoodList.get(((FoodCardViewHolder)holder).getAdapterPosition()).getId());
                        }
                    });


                    AlertDialog dialog = options_menu.create();
                    dialog.show();

                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }


    @Override
    public int getItemViewType(int position) {

        if (mFoodList.get(position).getType() == "text"){
            return ITEM_TEXT;
        } else if (mFoodList.get(position).getType() == "food") {
            return ITEM_FOOD;
        } else {
            System.out.println("the item was card");
            return ITEM_CARD;
        }

    }

}


class FoodCardViewHolder extends RecyclerView.ViewHolder{
    ImageView mImage;
    TextView mTitle;
    CardView mCardView;
    TextView price;
    Button addbtn;

    public FoodCardViewHolder(View itemView) {
        super(itemView);

        mImage = itemView.findViewById(R.id.foodimage);
        mTitle = itemView.findViewById(R.id.foodname);
        mCardView = itemView.findViewById(R.id.cardview);
        price= itemView.findViewById(R.id.price);
        addbtn = itemView.findViewById(R.id.addMealBtn);

    }
}

class cartViewHolder extends RecyclerView.ViewHolder{

    TextView name;
    TextView price;
    TextView addon;

    public cartViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.cartMealName);
        price = itemView.findViewById(R.id.cartPrice);
        addon = itemView.findViewById(R.id.cartadds);
    }


}


class foodTextView extends RecyclerView.ViewHolder{
//    EditText title;

    public foodTextView(View itemView) {
        super(itemView);

//        title = itemView.findViewById(R.id.recycler_edittext);

    }

}

