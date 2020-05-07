package com.cia.rfclibrary.Adapters.SubscriberDetailsLog;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cia.rfclibrary.R;

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

public class SummaryLogRVAdapter extends RecyclerView.Adapter<SummaryLogRVAdapter.LogVH> {

    Context context;
    ArrayList<String> months;
    public String message;

    public SummaryLogRVAdapter(Context context, ArrayList<String> months) {
        this.context = context;
        this.months = months;
    }

    @NonNull
    @Override
    public LogVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.view_log_card, parent, false);

        return new LogVH(listItemView, this.context, this.months);
    }

    @Override
    public void onBindViewHolder(@NonNull LogVH holder, int position) {

        holder.month.setText(this.months.get(position));

    }

    @Override
    public int getItemCount() {
        return this.months.size();
    }

    public class LogVH extends RecyclerView.ViewHolder{

        Context context;
        ArrayList<String> months;
        TextView month;

        public LogVH(View itemView, final Context context, final ArrayList<String> months) {
            super(itemView);

            this.context = context;
            this.months = months;

            this.month = itemView.findViewById(R.id.view_log_card_month);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO:

                }
            });
        }
    }

    private class GetCountAST extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String id = strings[0];
            String month = strings[1];

            HttpURLConnection httpURLConnection = null;
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {

                URL url = new URL(context.getString(R.string.get_log_url));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));

                String data = URLEncoder.encode("sub_id", "UTF-8") +"="+ URLEncoder.encode(id, "UTF-8") +"&"+
                        URLEncoder.encode("month", "UTF-8") +"="+ URLEncoder.encode(month, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();

                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String line;
                StringBuilder response = new StringBuilder();

                while((line =  bufferedReader.readLine()) != null){
                    response.append(line);
                }

                if(response.length() > 0){

                    JSONArray root = new JSONArray(response.toString());
                    int bookCount = 0;
                    int toyCount = 0;
                    for(int i = 0; i < root.length(); i++){
                        JSONObject iLog = root.getJSONObject(i);
                        if(iLog.getString("book_id").contains("NULL")){
                            toyCount += 1;
                        } else if(iLog.getString("toy_id").contains("NULL")){
                            bookCount += 1;
                        }
                    }
                    message = "Number of books taken: " + bookCount + "\nNumber of toys taken: " + toyCount;
                    return message;
                } else {
                    message = "Number of books taken: " + 0 + "\nNumber of toys taken: " + 0;
                    return message;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Sorry! Something went wrong when getting data:\n" + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "Sorry! Something went wrong when getting data:\n" + e.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return "Sorry! Something went wrong when getting data:\n" + e.toString();
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

    }

}
