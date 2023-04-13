package com.example.weatherforcast.History_search;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteItem extends ItemTouchHelper.Callback {
    private final ListCityAdapter mListCityAdapter;
    private OnSwipeListener listener;

    public SwipeToDeleteItem(ListCityAdapter mListCityAdapter, OnSwipeListener listener) {
        this.mListCityAdapter = mListCityAdapter;
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, viewHolder.getAdapterPosition());
    }
    public interface OnSwipeListener {
        void onSwiped (RecyclerView.ViewHolder viewHolder, int position);
    }
}
