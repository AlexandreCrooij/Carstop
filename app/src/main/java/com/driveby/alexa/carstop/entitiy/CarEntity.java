package com.driveby.alexa.carstop.entitiy;

import android.support.annotation.NonNull;

public class CarEntity {
    @NonNull
    private String numbersplate;
    private String driver;

    public CarEntity() {
    }

    public CarEntity(@NonNull String numbersplate, String driver) {
        this.numbersplate = numbersplate;
        this.driver = driver;
    }

    @NonNull
    public String getNumbersplate() {
        return numbersplate;
    }

    public void setNumbersplate(@NonNull String numbersplate) {
        this.numbersplate = numbersplate;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
