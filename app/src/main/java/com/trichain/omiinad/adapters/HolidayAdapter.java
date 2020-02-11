package com.trichain.omiinad.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trichain.omiinad.Entities.HolidayTable;
import com.trichain.omiinad.HolidayDetailActivity;
import com.trichain.omiinad.R;
import com.trichain.omiinad.RoomDB.DatabaseClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    List<HolidayTable> holidayList;
    Context context;

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
        final HolidayTable h= holidayList.get(position);
        String date=formatdate(h.getStartDate())+" - "+formatdate(h.getEndDate());
//        String date=h.getStartDate()+"-"+h.getEndDate();
        holder.tvName.setText(h.getName());
        holder.tv_holiday_date.setText(date);
        //TODO set the count
        photoCount(h.getId(),context,holder.holiday_image_count);
//        holder.holiday_image_count.setText(photoCount(h.getId(),context));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HolidayDetailActivity.class);
                intent.putExtra("holiday",h.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return holidayList.size();
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tv_holiday_date,holiday_image_count;
        ImageView imageView;
        public HolidayViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_holiday_date = itemView.findViewById(R.id.tv_holiday_date);
            holiday_image_count = itemView.findViewById(R.id.holiday_image_count);
            tvName = itemView.findViewById(R.id.tv_holiday_name);
            imageView=itemView.findViewById(R.id.holiday_item_image);
        }
    }
    public String formatdate(String a){
        DateTimeFormatter formatter = null;
        String formattedString="";
        String[] job= a.split(" ");
        formattedString=job[5]+" "+job[1]+" "+job[2];
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
            formatter = formatter.withLocale( Locale.US );  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
            LocalDate date = LocalDate.parse(a, formatter);
            formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
            formattedString = date.format(formatter);
        }*/
        return formattedString;
    }
    public void photoCount(final int a, final Context context, final TextView view){
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task

                final int holidayphotoCount = DatabaseClient
                        .getInstance(context)
                        .getAppDatabase()
                        .photoDao()
                        .getNumberofHolidayphotos(a);
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
}
