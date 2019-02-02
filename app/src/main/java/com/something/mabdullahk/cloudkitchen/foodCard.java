package com.something.mabdullahk.cloudkitchen;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mabdullahk on 27/09/2018.
 */

public class foodCard implements Serializable {
    String name;
    List<String> options;
    List<String> days;
    int price;
    String mealImageUrl;
    String id;
    String location;
    String type;

    public foodCard() {
        this.name = null;
        this.options = null;
        this.days = null;
        this.price = 0;
        this.mealImageUrl = null;
        this.id = null;
        this.location = null;
        this.type = "text";
    }

    public foodCard(String name, List<String> options, List<String> days, int price, String mealImageUrl, String id, String location, String type) {
        this.name = name;
        this.options = options;
        this.days = days;
        this.price = price;
        this.mealImageUrl = mealImageUrl;
        this.id = id;
        this.location = location;
        this.type = type;
    }

    public foodCard(foodCard card) {
        this.name = card.name;
        this.options = card.options;
        this.days = card.days;
        this.price = card.price;
        this.mealImageUrl = card.mealImageUrl;
        this.id = card.id;
        this.location = card.location;
        this.type = "card";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getMealImageUrl() {
        return mealImageUrl;
    }

    public void setMealImageUrl(String mealImageUrl) {
        this.mealImageUrl = mealImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
