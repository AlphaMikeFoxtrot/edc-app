package com.cia.rfclibrary.Adapters.SummaryLogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cia.rfclibrary.Classes.MonthlyLog;
import com.cia.rfclibrary.R;

import java.util.ArrayList;

public class MonthlyLogRVAdapter extends RecyclerView.Adapter<MonthlyLogRVAdapter.SummaryVH> {

    Context context;
    ArrayList<MonthlyLog> logs;

    public MonthlyLogRVAdapter(Context context, ArrayList<MonthlyLog> logs) {
        this.context = context;
        this.logs = logs;
    }

    @NonNull
    @Override
    public SummaryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.summary_card_view, parent, false);

        return new SummaryVH(listItemView, context, logs);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryVH holder, int position) {
        holder.name.setText(logs.get(position).getName());
        holder.toyCout.setText(logs.get(position).getToyCount());
        holder.bookCount.setText(logs.get(position).getBookCount());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public class SummaryVH extends RecyclerView.ViewHolder{

        TextView name, bookCount, toyCout;
        ArrayList<MonthlyLog> logs;
        Context context;

        public SummaryVH(View itemView, Context context, ArrayList<MonthlyLog> logs) {
            super(itemView);

            this.context = context;
            this.logs = logs;

            this.name = itemView.findViewById(R.id.summary_name);
            this.bookCount = itemView.findViewById(R.id.summary_book_count);
            this.toyCout = itemView.findViewById(R.id.summary_toy_count);
        }
    }

}
