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
import com.trichain.omiinad.ViewPlaceActivity;
import com.trichain.omiinad.entities.VisitedPlaceTable;
import com.trichain.omiinad.R;
import com.trichain.omiinad.roomDB.DatabaseClient;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.HolidayViewHolder> {

    List<VisitedPlaceTable> visitedPlaceTableList;
    Context context;

    public EventAdapter(List<VisitedPlaceTable> visitedPlaceTableList, Context context) {
        this.visitedPlaceTableList = visitedPlaceTableList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_list_content, parent, false);
        return new HolidayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        final VisitedPlaceTable h= visitedPlaceTableList.get(position);
        holder.tvName.setText(h.getName());
        holder.id_date.setText(h.getVisitDate());
        holder.id_day_m_year.setText(h.getVisitDate());
//        holder.id_people.setText(getPeople(position));
        getPeople(h.getId(),context,holder.id_people);
        getPhotos(h.getId(),context,holder.id_photos);
        getOnePhotos(h.getId(),context,holder.imageView);
//        holder.id_photos.setText(getPhotos(position,context,holder.id_photos));
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //context.startActivity(new Intent(context, HolidayDetailActivity.class));
//            }
//        });
        holder.one_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewPlaceActivity.class);
                intent.putExtra("place_id",h.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return visitedPlaceTableList.size();
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,id_date,id_day_m_year,id_people,id_photos;
        ImageView imageView;
        View one_place;
        public HolidayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.id_textSite);
            imageView=itemView.findViewById(R.id.imageNews);
            id_photos=itemView.findViewById(R.id.id_photos);
            id_people=itemView.findViewById(R.id.id_people);
            id_day_m_year=itemView.findViewById(R.id.id_day_m_year);
            id_date=itemView.findViewById(R.id.id_date);
            one_place=itemView.findViewById(R.id.one_place);
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

    private void getOnePhotos(final int a, final Context context, final ImageView view){
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task

                final String holidayphotoCount = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .photoDao()
                        .getLatestEventphotos(a);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //change View Data
                        Glide.with(context)
                                .load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/"+holidayphotoCount)
                                .fallback(R.drawable.japan)
                                .into(view);
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
}
