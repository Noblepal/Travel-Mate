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
import com.google.android.material.card.MaterialCardView;
import com.trichain.omiinad.HolidayDetailActivity;
import com.trichain.omiinad.R;
import com.trichain.omiinad.entities.HolidayTable;
import com.trichain.omiinad.roomDB.DatabaseClient;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.trichain.omiinad.Utils.formatDate;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    private List<HolidayTable> holidayList;
    private Context context;

    public HolidayAdapter(List<HolidayTable> holidayList, Context context) {
        this.holidayList = holidayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_holiday, parent, false);
        return new HolidayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        final HolidayTable h = holidayList.get(position);
        String date = formatDate(h.getStartDate()) + " - " + formatDate(h.getEndDate());
        holder.tvName.setText(h.getName());
        holder.tv_holiday_date.setText(date);

        //TODO set the count
        photoCount(h.getId(), context, holder.holiday_image_count);
        //holder.holiday_image_count.setText(photoCount(h.getId(),context));
        setImage(h.getId(), holder.imageView);
        holder.mcvHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HolidayDetailActivity.class);
                intent.putExtra("holiday", h.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return holidayList.size();
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tv_holiday_date, holiday_image_count;
        ImageView imageView;
        MaterialCardView mcvHoliday;

        HolidayViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_holiday_date = itemView.findViewById(R.id.tv_holiday_date);
            holiday_image_count = itemView.findViewById(R.id.holiday_image_count);
            tvName = itemView.findViewById(R.id.tv_holiday_name);
            imageView = itemView.findViewById(R.id.holiday_item_image);
            mcvHoliday = itemView.findViewById(R.id.mcvHoliday);
        }
    }


    private void setImage(final int holidayid, ImageView imageView) {
        class GetHolidays extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .photoDao()
                        .getLatestHolydayphotos(holidayid);
            }

            @Override
            protected void onPostExecute(String holidayphotoCount) {
                super.onPostExecute(holidayphotoCount);

                Glide.with(context)
                        .load(Environment.getExternalStorageDirectory().getAbsolutePath() + "/holidayImages/" + holidayphotoCount)
                        .fallback(R.drawable.ic_landscape)
                        .placeholder(R.drawable.ic_landscape)
                        .transition(withCrossFade(500))
                        .into(imageView);
            }
        }

        GetHolidays gh = new GetHolidays();
        gh.execute();
    }

    public void photoCount(final int a, final Context context, final TextView view) {
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task

                final int holidayphotoCount = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .photoDao()
                        .getNumberofHolidayphotos(a);
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
}
