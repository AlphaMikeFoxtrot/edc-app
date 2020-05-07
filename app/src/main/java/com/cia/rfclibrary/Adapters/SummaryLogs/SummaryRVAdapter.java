package com.cia.rfclibrary.Adapters.SummaryLogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cia.rfclibrary.Classes.Subscriber;
import com.cia.rfclibrary.R;

import java.util.ArrayList;

public class SummaryRVAdapter extends RecyclerView.Adapter<SummaryRVAdapter.SummaryVH> {

    Context context;
    ArrayList<Subscriber> subscribers;

    public SummaryRVAdapter(Context context, ArrayList<Subscriber> subscribers) {
        this.context = context;
        this.subscribers = subscribers;
    }

    @NonNull
    @Override
    public SummaryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.summary_card_view, parent, false);

        return new SummaryVH(listItemView, context, subscribers);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryVH holder, int position) {
        holder.name.setText(subscribers.get(position).getName());
        holder.toyCout.setText(subscribers.get(position).getToyCount());
        holder.bookCount.setText(subscribers.get(position).getBookCount());
    }

    @Override
    public int getItemCount() {
        return subscribers.size();
    }

    public class SummaryVH extends RecyclerView.ViewHolder{

        TextView name, bookCount, toyCout;
        ArrayList<Subscriber> subscribers;
        Context context;

        public SummaryVH(View itemView, Context context, ArrayList<Subscriber> subscribers) {
            super(itemView);

            this.context = context;
            this.subscribers = subscribers;

            this.name = itemView.findViewById(R.id.summary_name);
            this.bookCount = itemView.findViewById(R.id.summary_book_count);
            this.toyCout = itemView.findViewById(R.id.summary_toy_count);
        }
    }

}
