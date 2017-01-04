package com.reggieescobar.taigo.Helpers;

import android.graphics.Bitmap;

import com.reggieescobar.taigo.Models.MyAddress;

/*
import livemoments.io.test.Objects.MyAddress;
import livemoments.io.test.Objects.MyNotification;
import livemoments.io.test.Objects.User;
import livemoments.io.test.Objects.UserNotificationMeta; */


public class AppResultListener {
    public void onSuccess(String result){}

   /* public void onSuccess(MyNotification myNotification){}

    public void onSuccess(UserNotificationMeta userNotificationMeta){}

    public void onSuccess(Boolean result){}
    public void onSuccess(MyAddress address) {}
    public void onSuccess(User user){} */

    public void onError(Exception error){}
   // public void onSuccess(MyAddress address) {}


    ///public void onSuccess(String myAddress){}

    public void onImageFetchSuccess(Bitmap image) {}
    public void onImageFetchSuccess(byte[] data) {}
}
