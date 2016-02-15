package com.cargo.booking_details;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    public  static final String TAG = "User Local Store";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context)
    {
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0); //name of the sharepref file and 0 is default value(empty)
    }

public void storeUserData(UserDetails user) {//editing the shared pref file
    SharedPreferences.Editor spEditor = userLocalDatabase.edit();
    spEditor.putString("name", user.name);
    spEditor.putString("email", user.email);
    spEditor.putString("contact", user.contact);
    spEditor.putBoolean("LoggedIn",true);
    spEditor.commit();


}

    public UserDetails getLoggedInUser()
    {
        String name = userLocalDatabase.getString("name","");;
        String email = userLocalDatabase.getString("email", "");
        String contact = userLocalDatabase.getString("contact", "");


        UserDetails storedUser = new UserDetails(name,email,contact);
        return storedUser;

    }

    public void setLoggedIn(boolean loggedIn)
    {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn",loggedIn);

    }

    public boolean authenticate()
    {
        if(userLocalDatabase.getBoolean("LoggedIn", true)) {
            return true;
            }
        else
            {
                return true;
            }

        }
    }





