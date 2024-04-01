package com.tradetrack.cryptolist.dataModel;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "data_table",
        foreignKeys = @ForeignKey(entity = LiveCurrencyModel.class,
                parentColumns = "id",
                childColumns = "currencyId",
                onDelete = ForeignKey.CASCADE))
public class DataModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int currencyId;
    private String currentDate;
    private String buyPrice;
    private String buyQuantity;
    private String sellQuantity;
    private String sellPrice;
    private String payment;
    private String sale;
    private String earning;
    private String currencyCode;
    private String colorCode;

    private Boolean isChecked = false;

    public DataModel() {

    }

    public DataModel(int currencyId, String currencyCode, String colorCode , String date, String buyPrice, String buyQuantity,
                     String sellQuantity, String sellPrice, String payment, String sale, String earning, Boolean isChecked) {

        this.currencyId = currencyId;
        this.currencyCode = currencyCode;
        this.currentDate = date;
        this.colorCode = colorCode;
        this.buyPrice = buyPrice;
        this.buyQuantity = buyQuantity;
        this.sellQuantity = sellQuantity;
        this.sellPrice = sellPrice;
        this.payment = payment;
        this.sale = sale;
        this.earning = earning;
        this.isChecked = isChecked;
    }

    // Getters and setters for each field


    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(String buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public String getSellQuantity() {
        return sellQuantity;
    }

    public void setSellQuantity(String sellQuantity) {
        this.sellQuantity = sellQuantity;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getEarning() {
        return earning;
    }

    public void setEarning(String earning) {
        this.earning = earning;
    }

}
