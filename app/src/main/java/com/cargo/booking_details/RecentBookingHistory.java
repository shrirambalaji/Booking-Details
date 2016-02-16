package com.cargo.booking_details;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RecentBookingHistory extends Activity {
    private static final String SP_NAME = "userDetails" ;
    private  TextView mNew;
    private Button mLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_booking_history);

        UserLocalStore userLocalStore = new UserLocalStore(this);

       mNew = (TextView)findViewById(R.id.textViewNam);

         final SharedPreferences storedUserDetails = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String name = storedUserDetails.getString("name","");


        Toast.makeText(getApplicationContext(),"Logged in as "+name,Toast.LENGTH_SHORT).show();
        mLogout = (Button)findViewById(R.id.logout);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = storedUserDetails.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }

}
