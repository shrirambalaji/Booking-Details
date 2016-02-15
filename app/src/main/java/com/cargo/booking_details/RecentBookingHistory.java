package com.cargo.booking_details;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

public class RecentBookingHistory extends Activity {
    private static final String SP_NAME = "userDetails" ;
    private  TextView mNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_booking_history);

        UserLocalStore userLocalStore = new UserLocalStore(this);

       mNew = (TextView)findViewById(R.id.textViewNam);

        SharedPreferences storedUserDetails = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String name = storedUserDetails.getString("name","");


        Toast.makeText(getApplicationContext(),"Logged in as "+name,Toast.LENGTH_SHORT).show();

    }

}
