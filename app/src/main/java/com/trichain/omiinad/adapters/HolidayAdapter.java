package com.trichain.omiinad.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trichain.omiinad.HolidayDetailActivity;
import com.trichain.omiinad.R;

import java.util.ArrayList;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    ArrayList<String> holidayArrayList;
    Context context;

    public HolidayAdapter(ArrayList<String> holidayArrayList, Context context) {
        this.holidayArrayList = holidayArrayList;
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
        holder.tvName.setText("Japan");
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, HolidayDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return holidayArrayList.size();
    }

    class HolidayViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        ImageView imageView;
        public HolidayViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_holiday_name);
            imageView=itemView.findViewById(R.id.holiday_item_image);
        }
    }
}
