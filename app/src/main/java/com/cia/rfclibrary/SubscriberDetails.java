package com.cia.rfclibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cia.rfclibrary.Adapters.SubscriberDetailsLog.LogRVAdapter;
import com.cia.rfclibrary.Classes.Subscriber;
import com.cia.rfclibrary.NetworkUtils.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SubscriberDetails extends AppCompatActivity {

    TextView name, bookCount, toyCount, phone, dob, enrolledFor, is_ecre, is_external_ecd, enrolledOn, enrollmentType, reb, leb, center, gender, ecre_level, external_ecd_name, sub_class, school_board, m_name, m_qual, m_occ, m_phone, m_email, m_lang, f_name, f_qual, f_occ, f_phone, f_email, f_lang;    public static TextView id;
    LinearLayout viewMoreContainer;
    Button viewMoreButton;
    RecyclerView analytics;
    Subscriber subscriber;
    Intent intent;
    ProgressDialog progressDialog;
    ArrayList<String> months = new ArrayList<>();

    public static final String LOG_TAG = "sub_det_act";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_details);

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);
        flag = true;

        progressDialog = new ProgressDialog(this);

        intent = getIntent();

        subscriber = intent.getParcelableExtra("subscriber");

        name = findViewById(R.id.subscriber_detail_name);
        id = findViewById(R.id.subscriber_detail_id);
        bookCount = findViewById(R.id.subscriber_detail_book_count);
        toyCount = findViewById(R.id.subscriber_detail_toy_count);
        phone = findViewById(R.id.subscriber_detail_phone);
        dob = findViewById(R.id.subscriber_detail_dob);
        enrolledFor = findViewById(R.id.subscriber_detail_enrolled_for);
        enrolledOn = findViewById(R.id.subscriber_detail_enrolled_on);
        enrollmentType = findViewById(R.id.subscriber_detail_enrollment_type);
        reb = findViewById(R.id.subscriber_detail_reb);
        leb = findViewById(R.id.subscriber_detail_leb);
        center = findViewById(R.id.subscriber_detail_center);
        gender = findViewById(R.id.subscriber_detail_gender);
        is_ecre = findViewById(R.id.subscriber_detail_is_ecre);
        ecre_level = findViewById(R.id.subscriber_detail_ecre_level);
        is_external_ecd = findViewById(R.id.subscriber_detail_is_external_ecd);
        external_ecd_name = findViewById(R.id.subscriber_detail_external_ecd_name);
        sub_class = findViewById(R.id.subscriber_detail_class);
        school_board = findViewById(R.id.subscriber_detail_board);
        m_name = findViewById(R.id.subscriber_detail_m_name);
        m_qual = findViewById(R.id.subscriber_detail_m_qual);
        m_occ = findViewById(R.id.subscriber_detail_m_occ);
        m_phone = findViewById(R.id.subscriber_detail_m_phone);
        m_email = findViewById(R.id.subscriber_detail_m_email);
        m_lang = findViewById(R.id.subscriber_detail_m_lang);
        f_name = findViewById(R.id.subscriber_detail_f_name);
        f_qual = findViewById(R.id.subscriber_detail_f_qual);
        f_occ = findViewById(R.id.subscriber_detail_f_occ);
        f_phone = findViewById(R.id.subscriber_detail_f_phone);
        f_email = findViewById(R.id.subscriber_detail_f_email);
        f_lang = findViewById(R.id.subscriber_detail_f_lang);

        viewMoreContainer = findViewById(R.id.subscriber_detail_view_more_con);

        viewMoreButton = findViewById(R.id.subscriber_detail_view_more_button);

        analytics = findViewById(R.id.subscriber_detail_analytics_rv);
        analytics.setLayoutManager(new LinearLayoutManager(this));

        name.setText(subscriber.getName());
        id.setText(subscriber.getId());
        bookCount.setText(subscriber.getBookCount());
        toyCount.setText(subscriber.getToyCount());
        phone.setText(subscriber.getPhone());
        dob.setText(subscriber.getDob());
        enrolledFor.setText(subscriber.getEnrolledFor());
        enrolledOn.setText(subscriber.getEnrolledOn());
        enrollmentType.setText(subscriber.getEnrollmentType());
        reb.setText(subscriber.getReb());
        leb.setText(subscriber.getLeb());
        center.setText(subscriber.getCenter());
        gender.setText(subscriber.getGender());
        if(subscriber.getis_ecre().equals("1")){
            is_ecre.setText("True");
        } else if (subscriber.getis_ecre().equals("0")){
            is_ecre.setText("False");
        }
        ecre_level.setText(subscriber.getecre_level());
        if(subscriber.getis_external_ecd().equals("1")){
            is_external_ecd.setText("True");
        } else if(subscriber.getis_external_ecd().equals("0")){
            is_external_ecd.setText("False");
        }
        external_ecd_name.setText(subscriber.getexternal_ecd_name());
        sub_class.setText(subscriber.getSubscriber_class());
        school_board.setText(subscriber.getBoard());
        m_name.setText(subscriber.getm_name());
        m_qual.setText(subscriber.getm_qual());
        m_occ.setText(subscriber.getm_occ());
        m_phone.setText(subscriber.getm_phone());
        m_email.setText(subscriber.getm_email());
        m_lang.setText(subscriber.getm_lang());
        f_name.setText(subscriber.getf_name());
        f_qual.setText(subscriber.getf_qual());
        f_occ.setText(subscriber.getf_occ());
        f_phone.setText(subscriber.getf_phone());
        f_email.setText(subscriber.getf_email());
        f_lang.setText(subscriber.getf_lang());
        new GetMonthsAST().execute();

        viewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewMoreContainer.getVisibility() == View.VISIBLE) {
                    viewMoreButton.setText("view more");
                    viewMoreContainer.setVisibility(View.GONE);
                } else if(viewMoreContainer.getVisibility() == View.GONE){
                    viewMoreButton.setText("view less");
                    viewMoreContainer.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private class GetMonthsAST extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage(getString(R.string.ast_pd_message));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(getString(R.string.get_months_url));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

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
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, e.toString());
                return "";
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
            if(s.isEmpty() || s.contains("fail") || s.contains("[]")){

                Toast.makeText(SubscriberDetails.this, "Monthly data not available at the moment", Toast.LENGTH_LONG).show();

            } else {

                try {

                    JSONArray root = new JSONArray(s);
                    for(int i = 0; i < root.length(); i++){

                        JSONObject iLog = root.getJSONObject(i);
                        months.add(iLog.getString("month"));

                    }

                    LogRVAdapter adapter = new LogRVAdapter(SubscriberDetails.this, months);
                    analytics.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
