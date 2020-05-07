package com.cia.rfclibrary.Adapters.SubscriberDetailsLog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cia.rfclibrary.R;
import com.cia.rfclibrary.SubscriberDetails;

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
import java.util.concurrent.ExecutionException;

public class LogRVAdapter extends RecyclerView.Adapter<LogRVAdapter.LogVH> {

    Context context;
    ArrayList<String> months;
    public String message;

    public LogRVAdapter(Context context, ArrayList<String> months) {
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

                    new GetCountAST().execute(SubscriberDetails.id.getText().toString(), months.get(getAdapterPosition()));

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    try {
                        builder
                                .setTitle(months.get(getAdapterPosition()))
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setMessage(new GetCountAST().execute(SubscriberDetails.id.getText().toString(), months.get(getAdapterPosition())).get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        builder
                                .setTitle(months.get(getAdapterPosition()))
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setMessage(e.toString());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        builder
                                .setTitle(months.get(getAdapterPosition()))
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setMessage(e.toString());
                    }

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

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
                    JSONObject iLog = root.getJSONObject(0);
                    bookCount += Integer.parseInt(iLog.getString("book_count"));
                    toyCount += Integer.parseInt(iLog.getString("toy_count"));
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
