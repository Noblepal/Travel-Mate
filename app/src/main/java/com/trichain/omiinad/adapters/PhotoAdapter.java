package com.trichain.omiinad.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trichain.omiinad.Entities.PhotoTable;
import com.trichain.omiinad.Entities.VisitedPlaceTable;
import com.trichain.omiinad.R;
import com.trichain.omiinad.RoomDB.DatabaseClient;
import com.trichain.omiinad.ViewPlace;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.HolidayViewHolder> {

    List<PhotoTable> photoTables;
    Context context;

    public PhotoAdapter(List<PhotoTable> photoTables, Context context) {
        this.photoTables = photoTables;
        this.context = context;
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new HolidayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        final PhotoTable h= photoTables.get(position);
        getOnePhotos(h.getPhotoName(),context,holder.imageView);
    }

    @Override
    public int getItemCount() {
        return photoTables.size();
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public HolidayViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageNews);
        }
    }

    private void getPeople(final int a, final Context context, final TextView view){
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task

                final int holidayphotoCount = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .peopleDao()
                        .getNumberofEventPeople(a);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //change View Data
                        view.setText(String.valueOf(holidayphotoCount));
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    private void getPhotos(final int a, final Context context, final TextView view){
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task

                final int holidayphotoCount = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .photoDao()
                        .getNumberofEventphotos(a);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //change View Data
                        view.setText(String.valueOf(holidayphotoCount));
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    private void getOnePhotos(final String a, final Context context, final ImageView view){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //change View Data
                Glide.with(context)
                        .load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/"+a)
                        .fallback(R.drawable.japan)
                        .into(view);
            }
        });

    }
}
