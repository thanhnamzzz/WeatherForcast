package com.example.weatherforcast.hours_Weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherforcast.Global;
import com.example.weatherforcast.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.HoursViewHolder> {
    private ArrayList<ListHours> mListHours;
    private Context mConText;
    public HoursAdapter(ArrayList<ListHours> mListHours) {
//        this.mListHours = mListHours;
        this.mListHours = new ArrayList<>();
        this.mListHours.addAll(mListHours);
    }

    @NonNull
    @Override
    public HoursAdapter.HoursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mConText = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_next_time, parent,false);
        return new HoursViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HoursAdapter.HoursViewHolder holder, int position) {
        ListHours listHours = mListHours.get(position);
        holder.tvTime.setText(getHours(listHours.getDtTxt()) + ":00");
        Glide.with(mConText).load(Global.getImageDescriptionHours(listHours.getWeather().get(0).getIcon())).into(holder.imgDescription);
        holder.tvTemple.setText(Global.convertKtoC(listHours.getMain().getTemp()));
    }

    @Override
    public int getItemCount() {
        return mListHours.size();
    }

    public void updateData(ArrayList<ListHours> mListHours) {
        this.mListHours.clear();
        this.mListHours.addAll(mListHours);
        notifyDataSetChanged();;
    }

    public class HoursViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime,tvTemple;
        ImageView imgDescription;
        public HoursViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTemple = itemView.findViewById(R.id.tvTemple);
            imgDescription = itemView.findViewById(R.id.imgDescription);
        }
    }
    private static String getHours(String time) {
        TimeZone timeZone = TimeZone.getTimeZone("ICT");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm");
        dateFormat.setTimeZone(timeZone);
        try {
            Date date = dateFormat.parse(time);
            return date.getHours()+"";
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
