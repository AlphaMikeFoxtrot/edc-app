package com.cia.rfclibrary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cia.rfclibrary.Adapters.SubscriberDetailsLog.SummaryLogRVAdapter;
import com.cia.rfclibrary.Adapters.SummaryLogs.MonthRVAdapter;
import com.cia.rfclibrary.Adapters.SummaryLogs.MonthlyLogRVAdapter;
import com.cia.rfclibrary.Adapters.SummaryLogs.SummaryRVAdapter;
import com.cia.rfclibrary.Classes.MonthlyLog;
import com.cia.rfclibrary.Classes.Subscriber;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Summary extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView, analyticsRV;
    ProgressDialog progressDialog;
    int selectedItem;
    ArrayList<MonthlyLog> logs = new ArrayList<>();
    TextView error, title;
    ArrayList<String> months = new ArrayList<>();
    public static final String LOG_TAG = "summary_act";
    ArrayList<Subscriber> subscribers = new ArrayList<>();
    SummaryRVAdapter adapter;
    MonthlyLogRVAdapter monthlyLogRVAdapter;
    LinearLayout summaryCon, analyticsCon;

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
        setContentView(R.layout.activity_summary);

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);
        flag = true;

        title = findViewById(R.id.summary_toolbar_title);
        title.setText("Summary");
        summaryCon = findViewById(R.id.summary_container);
        analyticsCon = findViewById(R.id.analytics_con);
        error = findViewById(R.id.summary_error);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.ast_pd_message));
        analyticsRV = findViewById(R.id.analytics_rv);
        analyticsRV.setLayoutManager(new LinearLayoutManager(this));
        recyclerView = findViewById(R.id.summary_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.summary_toolbar);
        setSupportActionBar(toolbar);

        new GetSummaryAST().execute();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("summary_month_selected"));

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String monthSelected = intent.getStringExtra("month");
            new GetMonthlyAnalysis().execute(monthSelected);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.summary_menu_analytics){
            summaryCon.setVisibility(View.GONE);
            analyticsCon.setVisibility(View.VISIBLE);
            title.setText("Analytics");
            new GetMonthsAST().execute();
            return true;
        } else if(item.getItemId() == R.id.summary_filter){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.summary_sort_prompt, null);

            builder.setView(promptView);

            final Spinner spinner = promptView.findViewById(R.id.summary_prompt_spinner);
            final ArrayList<String> spinnerList = new ArrayList<>();
            spinnerList.add("by number of books");
            spinnerList.add("by number of toys");
            spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList));

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch(spinnerList.get(position)){

                        case "by number of books":
                            selectedItem = 0;
                            break;

                        case "by number of toys":
                            selectedItem = 1;
                            break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            builder
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sortSummary(selectedItem);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        return false;
    }

    private void sortSummary(int selectedItem) {

        switch (selectedItem){

            case 0:
                // books

                Collections.sort(subscribers, new Comparator<Subscriber>() {
                    @Override
                    public int compare(Subscriber o1, Subscriber o2) {
                        return o2.getBookCount().compareTo(o1.getBookCount());
                    }
                });

                ArrayList<Subscriber> newList = new ArrayList<>();
                newList = subscribers;
                adapter = new SummaryRVAdapter(this, newList);
                recyclerView.setAdapter(adapter);
                break;

            case 1:
                Collections.sort(subscribers, new Comparator<Subscriber>() {
                    @Override
                    public int compare(Subscriber o1, Subscriber o2) {
                        return o2.getToyCount().compareTo(o1.getToyCount());
                    }
                });

                ArrayList<Subscriber> newListTwo = new ArrayList<>();
                newList = subscribers;
                adapter = new SummaryRVAdapter(this, newList);
                recyclerView.setAdapter(adapter);
                break;

        }

    }

    public class GetSummaryAST extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog = new ProgressDialog(Summary.this);

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(getString(R.string.get_subscribers_raw_url));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){
                    response.append(line);
                }

                Log.v("LOOK FOR ME!!!!!!!!!!", "response: " + response);

                if(response.length() > 0){

                    JSONArray root = new JSONArray(response.toString());
                    for(int i = 0; i < root.length(); i++){

                        JSONObject iSubscriber = root.getJSONObject(i);
                        Subscriber subscriber = new Subscriber();
                        subscriber.setId(iSubscriber.getString("subscriber_id"));
                        subscriber.setName(iSubscriber.getString("subscriber_name"));
                        subscriber.setBookIssued(iSubscriber.getString("book_issued"));
                        subscriber.setToyIssued(iSubscriber.getString("toy_issued"));
                        subscriber.setIsGen(iSubscriber.getString("is_gen"));
                        subscriber.setIsToy(iSubscriber.getString("is_toy"));
                        subscriber.setEnrolledFor(iSubscriber.getString("subscriber_enrolled_for"));
                        subscriber.setEnrolledOn(iSubscriber.getString("subscriber_enrolled_on"));
                        subscriber.setEnrollmentType(iSubscriber.getString("subscriber_enrollement_type"));
                        subscriber.setLeb(iSubscriber.getString("subscriber_local_education_board"));
                        subscriber.setReb(iSubscriber.getString("subscriber_regional_education_board"));
                        subscriber.setCenter(iSubscriber.getString("subscriber_center"));
                        subscriber.setPhone(iSubscriber.getString("subscriber_phone"));
                        subscriber.setDob(iSubscriber.getString("subscriber_date_of_birth"));
                        subscriber.setGender(iSubscriber.getString("subscriber_gender"));
                        subscriber.setBookCount(iSubscriber.getString("book_count"));
                        subscriber.setToyCount(iSubscriber.getString("toy_count"));
                        subscribers.add(subscriber);

                    }

                } else {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                    error.setText("Summary is not available at the moment");
                }

                return null;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, e.toString());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, e.toString());
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v(LOG_TAG, e.toString());
                return null;
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
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            adapter = new SummaryRVAdapter(Summary.this, subscribers);
            recyclerView.setAdapter(adapter);
        }
    }

    private class GetMonthsAST extends AsyncTask<Void, Void, String>{
        ProgressDialog progressDialog = new ProgressDialog(Summary.this);

        @Override
        protected void onPreExecute() {
        Log.v("LOOKFORME!!!!", "inside onPreExecute");
            if(months.size() > 0){
                months.clear();
            }
            progressDialog.setMessage(getString(R.string.ast_pd_message));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
        Log.v("LOOKFORME!!!!", "inside doInBackground");
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            try {
        Log.v("LOOKFORME!!!!", "inside try block one at line 367");

                URL url = new URL(getString(R.string.get_months_url));
        Log.v("LOOKFORME!!!!", "using url: " + url);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();

                while((line = bufferedReader.readLine()) != null){
        Log.v("LOOKFORME!!!!", "going through line: " + line);
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

                Toast.makeText(Summary.this, "Monthly data not available at the moment", Toast.LENGTH_LONG).show();

            } else {

                try {

                    Toast.makeText(Summary.this, "trying to convert (s): " + s, Toast.LENGTH_LONG).show();
                    Log.v(LOG_TAG, "trying to convert (s): " + s);
                    JSONArray root = new JSONArray(s);
                    for(int i = 0; i < root.length(); i++){

                        JSONObject iLog = root.getJSONObject(i);
                        months.add(iLog.getString("month"));

                    }

                    MonthRVAdapter adapter = new MonthRVAdapter(Summary.this, months);
                    analyticsRV.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.v(LOG_TAG, e.toString());
                    Toast.makeText(Summary.this, "JSONException is this me????" + e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public class GetMonthlyAnalysis extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialogMonthlyAnalysis = new ProgressDialog(Summary.this);

        @Override
        protected void onPreExecute() {
            if(logs.size() > 0){
                logs.clear();
            }
            progressDialogMonthlyAnalysis.setMessage(getString(R.string.ast_pd_message));
            progressDialogMonthlyAnalysis.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String month = strings[0];

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            BufferedWriter bufferedWriter = null;

            try {

                URL url = new URL(getString(R.string.get_analytics_url));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));

                String data = URLEncoder.encode("month", "UTF-8") +"="+ URLEncoder.encode(month, "UTF-8");

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
                        Log.v(LOG_TAG, e.toString());
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v(LOG_TAG, s);
            progressDialogMonthlyAnalysis.dismiss();
            if(s.isEmpty()){
                Toast.makeText(Summary.this, "Sorry! Something went wrong when getting anaytics", Toast.LENGTH_SHORT).show();
            } else {

                try {

                    JSONArray root = new JSONArray(s);
                    for(int i = 0; i < root.length(); i++){

                        JSONObject iLog = root.getJSONObject(i);
                        MonthlyLog log = new MonthlyLog();
                        log.setId(iLog.getString("subscriber_id"));
                        log.setName(iLog.getString("subscriber_name"));
                        log.setMonth(iLog.getString("month"));
                        log.setBookCount(iLog.getString("book_count"));
                        log.setToyCount(iLog.getString("toy_count"));
                        logs.add(log);

                    }

                    monthlyLogRVAdapter = new MonthlyLogRVAdapter(Summary.this, logs);
                    summaryCon.setVisibility(View.VISIBLE);
                    analyticsCon.setVisibility(View.GONE);
                    recyclerView.setAdapter(monthlyLogRVAdapter);

                } catch (JSONException e) {
                    Toast.makeText(Summary.this, "Sorry! Something went wrong when getting anaytics: " + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }
    }

}
