package com.example.weatherforcast.History_search;

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
import com.example.weatherforcast.current_City.CurrentWeather;

import java.util.ArrayList;

public class ListCityAdapter extends RecyclerView.Adapter<ListCityAdapter.ListCityViewHolder> {
    private ArrayList<CurrentWeather> mListHistoryCity;
    private Context mConText;
    private IOnItemClickListener IOnItemClickListener;

    public ListCityAdapter(ArrayList<CurrentWeather> mListHistoryCity) {
        this.mListHistoryCity = new ArrayList<>();
        this.mListHistoryCity.addAll(mListHistoryCity);
    }

    @NonNull
    @Override
    public ListCityAdapter.ListCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mConText = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_city, parent,false);
        return new ListCityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCityAdapter.ListCityViewHolder holder, int position) {
        CurrentWeather currentWeather = mListHistoryCity.get(position);
        holder.tvCitySelect.setText(currentWeather.getName());
        holder.tvCurrentTempleCity.setText(Global.convertKtoC(currentWeather.getMain().getTemp()));
        holder.tvDescriptionCity.setText(currentWeather.getWeather().get(0).getDescription());
        holder.tvHightAndLow.setText("H: " + Global.convertKtoC(currentWeather.getMain().getTempMax())
                + " - L: " + Global.convertKtoC(currentWeather.getMain().getTempMin()));
        Glide.with(mConText).load(Global.getImageDescriptionHours(currentWeather.getWeather().get(0).getIcon())).into(holder.imgDescriptionCity);
    }

    @Override
    public int getItemCount() {
        return mListHistoryCity.size();
    }

    public void updateData(ArrayList<CurrentWeather> mListHistoryCity) {
        this.mListHistoryCity.clear();
        this.mListHistoryCity.addAll(mListHistoryCity);
        notifyDataSetChanged();;
    }

    public void setIOnItemClickListener(com.example.weatherforcast.History_search.IOnItemClickListener IOnItemClickListener) {
        this.IOnItemClickListener = IOnItemClickListener;
    }

    public class ListCityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgDescriptionCity;
        TextView tvCurrentTempleCity,tvHightAndLow,tvCitySelect,tvDescriptionCity;
        public ListCityViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDescriptionCity = itemView.findViewById(R.id.imgDescriptionCity);
            tvCurrentTempleCity = itemView.findViewById(R.id.tvCurrentTempleCity);
            tvHightAndLow = itemView.findViewById(R.id.tvHightAndLow);
            tvCitySelect = itemView.findViewById(R.id.tvCitySelect);
            tvDescriptionCity = itemView.findViewById(R.id.tvDescriptionCity);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            IOnItemClickListener.onItemClick(mListHistoryCity.get(getAdapterPosition()));
        }
    }
}
