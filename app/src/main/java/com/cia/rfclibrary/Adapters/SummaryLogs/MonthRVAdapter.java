package com.cia.rfclibrary.Adapters.SummaryLogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
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

public class MonthRVAdapter extends RecyclerView.Adapter<MonthRVAdapter.LogVH> {

    Context context;
    ArrayList<String> months;
    public String message;

    public MonthRVAdapter(Context context, ArrayList<String> months) {
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

                    Intent intent = new Intent("summary_month_selected");
                    //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                    intent.putExtra("month", months.get(getAdapterPosition()));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                }
            });
        }
    }

}
