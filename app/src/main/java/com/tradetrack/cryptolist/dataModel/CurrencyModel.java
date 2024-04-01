package com.tradetrack.cryptolist.dataModel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "currency_table")
public class CurrencyModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String currencyFullName;
    private String currencyShortcut;
    private String currencyLiveRate;
    private String currencyMaxRate;
    private String currencyIcon;
    private boolean isChecked = false;
    private boolean isBookMarked = false;

    public CurrencyModel(String currencyFullName, String currencyShortcut, String currencyLiveRate, String currencyMaxRate, String currencyIcon, boolean isChecked, boolean isBookMarked) {
        this.currencyFullName = currencyFullName;
        this.currencyShortcut = currencyShortcut;
        this.currencyLiveRate = currencyLiveRate;
        this.currencyMaxRate = currencyMaxRate;
        this.currencyIcon = currencyIcon;
        this.isChecked = isChecked;
        this.isBookMarked = isBookMarked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyFullName() {
        return currencyFullName;
    }

    public void setCurrencyFullName(String currencyFullName) {
        this.currencyFullName = currencyFullName;
    }

    public String getCurrencyShortcut() {
        return currencyShortcut;
    }

    public void setCurrencyShortcut(String currencyShortcut) {
        this.currencyShortcut = currencyShortcut;
    }

    public String getCurrencyLiveRate() {
        return currencyLiveRate;
    }

    public void setCurrencyLiveRate(String currencyLiveRate) {
        this.currencyLiveRate = currencyLiveRate;
    }

    public String getCurrencyMaxRate() {
        return currencyMaxRate;
    }

    public void setCurrencyMaxRate(String currencyMaxRate) {
        this.currencyMaxRate = currencyMaxRate;
    }

    public String getCurrencyIcon() {
        return currencyIcon;
    }

    public void setCurrencyIcon(String currencyIcon) {
        this.currencyIcon = currencyIcon;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isBookMarked() {
        return isBookMarked;
    }

    public void setBookMarked(boolean bookMarked) {
        isBookMarked = bookMarked;
    }
}
