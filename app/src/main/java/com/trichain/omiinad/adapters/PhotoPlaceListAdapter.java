package com.trichain.omiinad.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trichain.omiinad.activities.DetailGalleryActivity;
import com.trichain.omiinad.R;
import com.trichain.omiinad.entities.PhotoTable;
import com.trichain.omiinad.entities.VisitedPlaceTable;
import com.trichain.omiinad.room.DatabaseClient;

import java.io.Serializable;
import java.util.List;

public class PhotoPlaceListAdapter extends RecyclerView.Adapter<PhotoPlaceListAdapter.HolidayViewHolder> {

    List<PhotoTable> photoTables;
    Context context;
    boolean isImageFitToScreen,isDate,isHoliday,isPlace,descending;
    String type;
    String TAG="PhotoPlaceListAdapter";
    RecyclerView mRecyclerView;
    private final View.OnClickListener mOnClickListener = new MyOnClickListener();

    public PhotoPlaceListAdapter(List<PhotoTable> photoTables, Context context, boolean isDate, boolean isHoliday, boolean isPlace, boolean descending, RecyclerView recyclerView) {
        this.photoTables = photoTables;
        this.context = context;
        this.isDate = isDate;
        this.isHoliday = isHoliday;
        this.isPlace = isPlace;
        this.mRecyclerView=recyclerView;
        this.descending = descending;
        if (isDate){
            this.type="date";
        }else if (isHoliday){
            this.type="holiday";
        }else if (isPlace){
            this.type="place";
        }

    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: 222" );
            }
        });
        return new HolidayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {

        final PhotoTable h = photoTables.get(position);
        getOnePhotos(h.getPhotoName(), context, holder.imageView);
        holder.photoName.setText(h.getPhotoName());
        getName(h.getPlaceID(),context,holder.photoName);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

    }

    @Override
    public int getItemCount() {
        return photoTables.size();
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView photoName;
        View parental;

        public HolidayViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageNews);
            photoName = itemView.findViewById(R.id.tvPhotoName);
            parental = itemView.findViewById(R.id.this_is_one_item);
        }
    }

    private void getName(final int a, final Context context, final TextView view) {
        class SaveTask extends AsyncTask<Void, Void, VisitedPlaceTable> {

            @Override
            protected VisitedPlaceTable doInBackground(Void... voids) {

                final VisitedPlaceTable visitedPlaceTable = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .visitedPlaceDao()
                        .getHolidayIdofplace(a);
                return visitedPlaceTable;
            }

            @Override
            protected void onPostExecute(VisitedPlaceTable aVoid) {
                super.onPostExecute(aVoid);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //change View Data
                        view.setText(aVoid.getName());
                    }
                });
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    private void getPhotos(final int a, final Context context, final TextView view) {
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task

                final int holidayphotoCount = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .photoDao()
                        .getNumberofEventphotos(a);
                ((Activity) context).runOnUiThread(new Runnable() {
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

    private void getOnePhotos(final String a, final Context context, final ImageView view) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //change View Data
                Glide.with(context)
                        .load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + a)
                        .fallback(R.drawable.japan)
                        .into(view);
            }
        });

    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position = mRecyclerView.getChildLayoutPosition(v);
            final PhotoTable h = photoTables.get(position);
            Log.e(TAG, "onClick: "+type+descending+h.getHolidayID() );
            Intent intent= new Intent(context, DetailGalleryActivity.class);
            intent.putExtra("type",type);
            intent.putExtra("descending",descending);
            intent.putExtra("date",h.getId());
            intent.putExtra("holiday",h.getHolidayID());
            intent.putExtra("place",h.getPlaceID());
            intent.putExtra("id",h.getId());
            intent.putExtra("data", (Serializable) photoTables);
            context.startActivity(intent);
        }
    }
}
