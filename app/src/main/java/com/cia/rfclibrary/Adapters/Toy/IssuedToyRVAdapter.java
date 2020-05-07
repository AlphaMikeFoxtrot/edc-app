package com.cia.rfclibrary.Adapters.Toy;

import android.app.ProgressDialog;
import android.content.Context;
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

import com.cia.rfclibrary.Adapters.Book.IssuedBookRVAdapter;
import com.cia.rfclibrary.Classes.Toy;
import com.cia.rfclibrary.HomeScreen;
import com.cia.rfclibrary.R;

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

public class IssuedToyRVAdapter extends RecyclerView.Adapter<IssuedToyRVAdapter.IssuedToyViewHolder> {

    Context context;
    ArrayList<Toy> toys;
    private static final String LOG_TAG = "issued_toy_rv";

    public IssuedToyRVAdapter(Context context, ArrayList<Toy> toys) {
        this.context = context;
        this.toys = toys;
    }

    @NonNull
    @Override
    public IssuedToyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_issued_stuff_card, parent, false);
        return new IssuedToyViewHolder(listItemView, this.context, this.toys);

    }

    @Override
    public void onBindViewHolder(@NonNull IssuedToyViewHolder holder, int position) {

        holder.toyId.setText(this.toys.get(position).getToyId());
        holder.subId.setText(this.toys.get(position).getIssuedToName());

    }

    @Override
    public int getItemCount() {
        return this.toys.size();
    }

    public class IssuedToyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView toyId, subId;
        Context context;
        ImageButton overflow;
        ArrayList<Toy> toys;

        public IssuedToyViewHolder(View itemView, Context context, ArrayList<Toy> toys) {
            super(itemView);

            this.toyId = itemView.findViewById(R.id.view_issued_stuff_obj_id);
            this.subId = itemView.findViewById(R.id.view_issued_stuff_sub_id);
            this.overflow = itemView.findViewById(R.id.view_issued_stuff_overflow_menu);

            this.context = context;
            this.toys = toys;

            this.overflow.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            PopupMenu popup = new PopupMenu(context, overflow);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.issue_pop_up_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    new ProtocolAST().execute("return", "toy", toys.get(getAdapterPosition()).getToyId(), toys.get(getAdapterPosition()).getIssuedToId());
                    return true;
                }
            });

            popup.show();//showing popup menu

        }

        private class ProtocolAST extends AsyncTask<String, Void, String> {

            public ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("running protocol...");
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                String protocol = strings[0];
                String type = strings[1];
                String obj_id = strings[2];
                String sub_id = strings[3];

                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;
                BufferedWriter bufferedWriter = null;

                try {

                    URL url = new URL(context.getString(R.string.protocol_url));
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));

                    String data = URLEncoder.encode("protocol", "UTF-8") +"="+ URLEncoder.encode(protocol, "UTF-8") +"&"+
                            URLEncoder.encode("type", "UTF-8") +"="+ URLEncoder.encode(type, "UTF-8") +"&"+
                            URLEncoder.encode("id", "UTF-8") +"="+ URLEncoder.encode(obj_id, "UTF-8") +"&"+
                            URLEncoder.encode("sub", "UTF-8") +"="+ URLEncoder.encode(sub_id, "UTF-8");

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
                if(s.contains("success")){
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                    Intent toHome = new Intent(context, HomeScreen.class);
                    toHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(toHome);
                } else {
                    Toast.makeText(context, "Something went wrong when running protocol:\n" + s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
