package com.driveby.alexa.carstop.entitiy;

import android.support.annotation.NonNull;

import com.driveby.alexa.carstop.model.Alert;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class AlertEntitiy implements Alert {

    @NonNull
    private String uid;
    private String date;
    private String platenumber;

    public AlertEntitiy(){

    }

    public AlertEntitiy(Alert alert){
        uid = alert.getUid();
        date = alert.getDate();
        platenumber = alert.getPlatenumber();
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getPlatenumber() {
        return platenumber;
    }

    public void setPlatenumber(String platenumber) {
        this.platenumber = platenumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof AlertEntitiy)) return false;
        AlertEntitiy o = (AlertEntitiy) obj;
        return o.getUid().equals(this.getUid());
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("platenumber", platenumber);

        return result;
    }
}
