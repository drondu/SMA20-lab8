package com.example.smartwallet;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MothlyStruct {

    public String month;
    private float expenses, income;

    public MothlyStruct() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public MothlyStruct(String month, float expenses, float income) {
        this.month = month;
        this.income = income;
        this.expenses = expenses;
    }

    public String getMonth() {
        return month;
    }

    public float getExpenses() {
        return expenses;
    }

    public float getIncome() {
        return income;
    }
}
