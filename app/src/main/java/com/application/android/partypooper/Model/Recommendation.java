package com.application.android.partypooper.Model;

public class Recommendation {

    private int amount;
    private String item;

    public Recommendation(int amount, String item) {
        this.amount = amount;
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
