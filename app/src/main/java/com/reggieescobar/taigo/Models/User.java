package com.reggieescobar.taigo.Models;

import com.google.gson.internal.Streams;
import com.reggieescobar.taigo.Helpers.AppPrefs;

/**
 * Created by prodoxx on 06/01/17.
 */

public class User {

    private static User instance;
    private String firstName;
    private String lastName;
    private String email;
    private String uid;
    private String accountType;
    private String licenseNum;

    private User(){}

    public static User getInstance() {
        if(instance != null){
            return instance;
        }else{
            instance = new User();
            return instance;
        }
    }



    public void setFirstName(String fname){
        firstName = fname;
    }

    public void setLastName(String lname){
        lastName = lname;
    }

    public void setEmail(String e){
        email = e;
    }

    public void setUid(String id ){
        uid = id;
    }

    public void setAccountType(String type){
        accountType = type;
    }

    public void setLicenseNum(String num){
        licenseNum = num;
    }



    public String getFirstName(){
        return  firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail(){
        return email;
    }
    public String getUid(){
        return uid;
    }


    public  String getAccountType(){
        return accountType;
    }

    public String getLicenseNum(){
        return licenseNum;
    }








}
