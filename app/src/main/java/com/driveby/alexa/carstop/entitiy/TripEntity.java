package com.driveby.alexa.carstop.entitiy;

import android.support.annotation.NonNull;

public class TripEntity {
    @NonNull
    private String uid;
    private String imageUrl;
    private String idCar;
    private String idUser;
    private String note;
    private boolean wasSafe;
    private String startDate;
    private String endDate;
    private int amountOfPassengers;

    public TripEntity() {
    }

    public TripEntity(@NonNull String uid, String imageUrl, String idCar,
                      String idUser, String note, boolean wasSafe,
                      String startDate, String endDate, int amountOfPassengers) {
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.idCar = idCar;
        this.idUser = idUser;
        this.note = note;
        this.wasSafe = wasSafe;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amountOfPassengers = amountOfPassengers;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIdCar() {
        return idCar;
    }

    public void setIdCar(String idCar) {
        this.idCar = idCar;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isWasSafe() {
        return wasSafe;
    }

    public void setWasSafe(boolean wasSafe) {
        this.wasSafe = wasSafe;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getAmountOfPassengers() {
        return amountOfPassengers;
    }

    public void setAmountOfPassengers(int amountOfPassengers) {
        this.amountOfPassengers = amountOfPassengers;
    }

}
