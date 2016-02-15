package com.cargo.booking_details;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; /*TAG is used necessarily for logging in logcat bcoz rather than writing getClass().getName() at each place where a log is placed in a particular activity, it is always preferred to have a TAG that would represent the name of the activity class.
   When you are running your application there might be more than one Activity class in it. To distinguish which activity class has logged the information in logcat we use a TAG which of course represents the name of the class.

*/
    private static final String PREF_NAME = "Get Token";
    private static final String SP_NAME = "userDetails";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;
    private  TextView mEditTextName;
    private  TextView mEditTextEmail;
    private  TextView mEditTextContact;
    UserLocalStore userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences userLocalDatabase = getSharedPreferences(SP_NAME,MODE_PRIVATE);


     boolean logged =  userLocalDatabase.getBoolean("LoggedIn",false);
        Log.i(TAG,"Login status : "+logged);
        if(logged == true)
        {

           Intent BookingHistory = new Intent(getApplicationContext(),RecentBookingHistory.class);
            startActivity(BookingHistory);
            finish();

        }

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
       mEditTextName = (TextView)findViewById(R.id.editTextName);
        mEditTextEmail = (TextView) findViewById(R.id.editTextEmail);

         mEditTextContact = (TextView) findViewById(R.id.editTextContact);;
        Button btnReg = (Button) findViewById(R.id.btnReg);



        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);


                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        userLocalStore = new UserLocalStore(this);

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

      btnReg.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              String name = mEditTextName.getText().toString();
              String email = mEditTextEmail.getText().toString();
              String contact = mEditTextContact.getText().toString();
              SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
              String token = sharedPreferences.getString("token", "not found"); //first value is the key to be found , the nxt is the default value
              String mobile_registered = "1";
              Log.i(TAG, "Token:" + token);

              if ((name.length() <= 0) || (email.length() <= 0) || mEditTextContact.getText().length() < 0) {
                  Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
              }

              if ((mEditTextContact.getText().length() > 13)) {
                  Toast.makeText(getApplicationContext(), "Please enter valid number ", Toast.LENGTH_SHORT).show();
              }
              UserDetails user = new UserDetails(name, email, contact);
              userLocalStore.storeUserData(user);




              SendDataToServer send = new SendDataToServer();
              send.execute(name, email, String.valueOf(contact), token, mobile_registered); //executing the async task with three parameters.


          }
      });






    }

    public  class SendDataToServer extends AsyncTask<String,String,String>{


        @Override
        protected String doInBackground(String... params) { //... specifies zero or more objects may be specified as parameters


            try {
                URL url = new URL("https://booking-cargologistics.rhcloud.com/android_register.php");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                Map<String,Object> hashMap = new LinkedHashMap<>();
                hashMap.put("android_name", params[0]); //first parameter in
                hashMap.put("android_email",params[1]);
                hashMap.put("android_contact",params[2]);
                hashMap.put("user_token",params[3]);
                hashMap.put("mobile_registered",params[4]);


                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : hashMap.entrySet()) { //for each entry in the Map entry set the string param is used to refernce the value . dont confuse with params in doinBackground
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8")); //specify encoding of key
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8")); //specify encodding of value
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                connection.setDoOutput(true);
                connection.getOutputStream().write(postDataBytes);
//getting the response from the server.

                Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                for ( int c = in.read(); c != -1; c = in.read() )
                    System.out.print((char)c);

            }


            catch (Exception e)
            {
                e.printStackTrace();
            }
        return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"Registered Successfully", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume () {
            super.onResume();
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

}
