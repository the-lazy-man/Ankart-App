package com.example.testingandroid;
public class DataModel {
    private static final String DEFAULT_ITEM_IMAGE = "https://image.shutterstock.com/image-vector/everyday-carry-stuff-travel-tourist-600w-2104852556.jpg";
    private final String name;
    private final float price;

    private final String imgURL;
    private int quantity;
    public DataModel(String name, float price, int quantity, String imgURL) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imgURL = imgURL;
    }

    public DataModel(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imgURL = DEFAULT_ITEM_IMAGE;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
    public String getImgURL() {
        return imgURL;
    }

}
