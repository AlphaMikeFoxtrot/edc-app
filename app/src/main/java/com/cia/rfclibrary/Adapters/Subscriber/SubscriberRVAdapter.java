package com.cia.rfclibrary.Adapters.Subscriber;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cia.rfclibrary.Adapters.Book.BookRVAdapter;
import com.cia.rfclibrary.Classes.Subscriber;
import com.cia.rfclibrary.HomeScreen;
import com.cia.rfclibrary.R;
import com.cia.rfclibrary.SubscriberDetails;

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

public class SubscriberRVAdapter extends RecyclerView.Adapter<SubscriberRVAdapter.SubscriberVH> {

    Context context;
    public static final String LOG_TAG = "sub_rv_ada";
    ArrayList<Subscriber> subscribers;

    public SubscriberRVAdapter(Context context, ArrayList<Subscriber> subscribers) {
        this.context = context;
        this.subscribers = subscribers;
    }

    @NonNull
    @Override
    public SubscriberVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listItemView = LayoutInflater.from(context).inflate(R.layout.view_subscriber_card, parent, false);
        return new SubscriberVH(listItemView, this.context, this.subscribers);

    }

    @Override
    public void onBindViewHolder(@NonNull SubscriberVH holder, int position) {

        holder.name.setText(this.subscribers.get(position).getName());
        holder.id.setText(this.subscribers.get(position).getId());

    }

    @Override
    public int getItemCount() {
        return subscribers.size();
    }

    public class SubscriberVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name, id;
        ImageButton overflow;
        Context context;
        ArrayList<Subscriber> subscribers;

        public SubscriberVH(View itemView, final Context context, final ArrayList<Subscriber> subscribers) {
            super(itemView);

            this.context = context;
            this.subscribers = subscribers;

            this.name = itemView.findViewById(R.id.view_subscriber_name);
            this.id = itemView.findViewById(R.id.view_subscriber_id);
            this.overflow = itemView.findViewById(R.id.view_subscriber_overflow);

            this.overflow.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                Intent toDetails = new Intent(context, SubscriberDetails.class);
                toDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                toDetails.putExtra("subscriber", subscribers.get(getAdapterPosition()));



                context.startActivity(toDetails);

                }
            });

        }

        @Override
        public void onClick(View v) {

            PopupMenu popup = new PopupMenu(context, overflow);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.subscriber_popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.subscriber_popup_delete) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("WARNING");
                        builder.setMessage("Are you sure you want to delete the subscriber?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DeleteAST().execute("subscriber", subscribers.get(getAdapterPosition()).getId());
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                    return true;
                }
            });

            popup.show();//showing popup menu

        }

        private class DeleteAST extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog = new ProgressDialog(context);

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("please wait...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {

                String type = strings[0];
                String obj_id = strings[1];

                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;
                BufferedWriter bufferedWriter = null;

                try {

                    URL url = new URL(context.getString(R.string.delete_url));
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();

                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));

                    String data = URLEncoder.encode("type", "UTF-8") +"="+ URLEncoder.encode(type, "UTF-8") +"&"+
                            URLEncoder.encode("obj_id", "UTF-8") +"="+ URLEncoder.encode(obj_id, "UTF-8");

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
                            return "";
                        }
                    }
                }

            }

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                if(s.isEmpty()){
                    Toast.makeText(context, "Sorry! Something went wrong when deleting: \n" + s, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                    Intent toHome = new Intent(context, HomeScreen.class);
                    toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(toHome);
                }
            }
        }

    }

}
