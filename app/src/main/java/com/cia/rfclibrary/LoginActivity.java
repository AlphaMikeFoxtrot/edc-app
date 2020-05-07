package com.cia.rfclibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cia.rfclibrary.Classes.Admin;
import com.cia.rfclibrary.NetworkUtils.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button login;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Admin admin;
    public static final String LOG_TAG = "login_log_tag";
    ProgressDialog progressDialog;

    NetworkChangeReceiver receiver;
    Boolean flag = false;
    IntentFilter filter;

    @Override
    protected void onStop() {
        super.onStop();
        if(flag) {
            unregisterReceiver(receiver);
            flag = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(flag) {
            unregisterReceiver(receiver);
            flag = false;
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);
        flag = true;

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login_button);

        sharedPreferences = this.getSharedPreferences(getString(R.string.sp_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(loggedIn()){

            // already logged in
            Toast.makeText(this, "logged in as " + sharedPreferences.getString(getString(R.string.sp_username), ""), Toast.LENGTH_SHORT).show();
            Intent toHome = new Intent(this, HomeScreen.class);
            toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toHome);

        } else {

            // not logged in
            LinearLayout linearLayout = findViewById(R.id.login_form);
            linearLayout.setVisibility(View.VISIBLE);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkLogin();
                }
            });

        }

    }

    private void checkLogin() {

        if(username.getText().toString().isEmpty()){

            username.setError("field cannot be empty");

        } else if(password.getText().toString().isEmpty()){

            password.setError("field cannot be empty");

        } else if(username.getText().toString().length() > 4 && password.getText().toString().length() > 4){

            login(username.getText().toString(), password.getText().toString());

        } else if(username.getText().toString().length() < 4){

            username.setError("field length cannot be less than 4");

        } else if(password.getText().toString().length() < 4){

            password.setError("field length cannot be less than 4");

        } else {

            Toast.makeText(this, "something went wrong with the login form\nPlease check all the fields and try again", Toast.LENGTH_SHORT).show();
            username.setError("");
            password.setError("");

        }
    }

    private void login(String username, String password) {
        new CheckLoginAST().execute(username, password);
    }

    private boolean loggedIn() {

        if(sharedPreferences.getBoolean(getString(R.string.sp_session), false)){
            return true;
        } else {
            return false;
        }

    }

    private class CheckLoginAST extends AsyncTask<String, Void, String> {

        public String username_login;
        public String password_login;
        public String id;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getString(R.string.ast_pd_message));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            username_login = strings[0];
            password_login = strings[1];

            HttpURLConnection httpURLConnection = null;
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(getString(R.string.check_login_url));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));

                String data = URLEncoder.encode("username", "UTF-8") +"="+ URLEncoder.encode(username_login, "UTF-8") +"&"+
                        URLEncoder.encode("password", "UTF-8") +"="+ URLEncoder.encode(password_login, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();

                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){
                    response.append(line);
                }

                return response.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, e.toString());
                return "URL: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, e.toString());
                return "IO: " + e.toString();
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(s.contains("auth_fail")){
                Toast.makeText(LoginActivity.this, "username and password incorrect", Toast.LENGTH_SHORT).show();
                username.setError("INCORRECT");
                password.setError("INCORRECT");
            } else if(s.contains("username")){
                Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();

                try {

                    JSONArray root = new JSONArray(s);
                    JSONObject user = root.getJSONObject(0);
                    editor.putString(getString(R.string.sp_username), user.getString("username"));
                    editor.putString(getString(R.string.sp_clearance), user.getString("clearance"));

                    boolean sess;
                    if(user.getString("session").contains("0")){
                        sess = false;
                    } else {
                        sess = true;
                    }
                    editor.putBoolean(getString(R.string.sp_session), sess);
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent toHome = new Intent(LoginActivity.this, HomeScreen.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toHome);
            }
        }
    }
}
