package com.driveby.alexa.carstop.entitiy;

import android.support.annotation.NonNull;

import com.driveby.alexa.carstop.model.User;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserEntity implements User {

    @NonNull
    private String uid;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Boolean isAdmin;

    public UserEntity(){

    }

    public UserEntity(User user){
        uid = user.getUid();
        firstname = user.getFirstname();
        lastname = user.getLastname();
        email = user.getEmail();
        phone = user.getPhone();
        isAdmin = user.isAdmin();
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof UserEntity)) return false;
        UserEntity o = (UserEntity) obj;
        return o.getUid().equals(this.getUid());
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstname", firstname);
        result.put("lastname", lastname);
        result.put("phone", phone);

        return result;
    }
}
