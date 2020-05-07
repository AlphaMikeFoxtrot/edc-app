package com.cia.rfclibrary.Adapters.Book;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cia.rfclibrary.Classes.Book;
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

public class BookRVAdapter extends RecyclerView.Adapter<BookRVAdapter.IssuedBookViewHolder> {

    Context context;
    private ArrayList<Book> books = new ArrayList<>();
    public static final String LOG_TAG = "book_rv_ada";

    public BookRVAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public IssuedBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_stuff_card, parent, false);
        return new IssuedBookViewHolder(listItemView, this.context, this.books);

    }

    @Override
    public void onBindViewHolder(@NonNull IssuedBookViewHolder holder, int position) {

        holder.bookId.setText(this.books.get(position).getBookId());
        holder.bookName.setText(this.books.get(position).getBookName());
        String isIssued = this.books.get(position).getIsIssued();

        if(isIssued.contains("1")){
            holder.isIssued.setText("TRUE");
            holder.issuedToCon.setVisibility(View.VISIBLE);
            holder.issuedTo.setText(this.books.get(position).getIssuedToName());
        } else {
            holder.isIssued.setText("FALSE");
        }

    }

    @Override
    public int getItemCount() {
        return this.books.size();
    }

    public class IssuedBookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView bookId, bookName, isIssued, issuedTo;
        LinearLayout issuedToCon;
        ImageButton overflow;
        Context context;
        ArrayList<Book> books;

        public IssuedBookViewHolder(View itemView, Context context, ArrayList<Book> books) {
            super(itemView);

            this.bookId = itemView.findViewById(R.id.view_stuff_obj_id);
            this.bookName = itemView.findViewById(R.id.view_stuff_obj_name);
            this.isIssued = itemView.findViewById(R.id.view_stuff_is_issued);
            this.issuedToCon = itemView.findViewById(R.id.view_stuff_issued_to_container);
            this.issuedTo = itemView.findViewById(R.id.view_stuff_issued_to);
            this.overflow = itemView.findViewById(R.id.view_stuff_overflow_menu);

            this.overflow.setOnClickListener(this);

            this.context = context;
            this.books = books;

        }

        @Override
        public void onClick(View v) {

            PopupMenu popup = new PopupMenu(context, overflow);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.pop_up_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("WARNING");
                    builder.setMessage("Are you sure you want to delete the book?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteAST().execute("book", books.get(getAdapterPosition()).getBookId());
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return true;
                }
            });

            popup.show();//showing popup menu

        }

        private class DeleteAST extends AsyncTask<String, Void, String>{

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
