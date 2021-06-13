package com.studio.classes;

import android.net.Uri;

import java.sql.Date;

public class Clothes {
    private String clothesType;
    private String color;
    private String category;
    private String pattern;
    private Date receiptDate;
    private String price;
    private String filePath;
    private String fileName;
    private Uri photoUri;
    private String drawerName;

    public Clothes(String clothesType, String color, String category, String pattern, Date receiptDate,
                   String price, String filePath, String fileName, String drawerName, Uri photoUri) {
        this.clothesType = clothesType;
        this.color = color;
        this.category = category;
        this.pattern = pattern;
        this.receiptDate = receiptDate;
        this.price = price;
        this.filePath = filePath;
        this.fileName = fileName;
        this.drawerName = drawerName;
        this.photoUri = photoUri;
    }

    public void setClothesType(String clothesType) {
        this.clothesType = clothesType;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getClothesType() {
        return clothesType;
    }

    public String getColor() {
        return color;
    }

    public String getPattern() {
        return pattern;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public String getPrice() {
        return price;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDrawerName() {
        return drawerName;
    }

    public void setDrawerName(String drawerName) {
        this.drawerName = drawerName;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}
