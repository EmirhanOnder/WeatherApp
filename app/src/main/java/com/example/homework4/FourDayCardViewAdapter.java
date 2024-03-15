package com.example.homework4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FourDayCardViewAdapter extends RecyclerView.Adapter<FourDayCardViewAdapter.viewHolder> {

    ArrayList<FourDayCardView> items;
    Context context;

    public FourDayCardViewAdapter(ArrayList<FourDayCardView> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FourDayCardViewAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_viewholder,parent,false);
        context = parent.getContext();
        return new viewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FourDayCardViewAdapter.viewHolder holder, int position) {
        holder.dayTxt.setText(items.get(position).getDay());
        holder.tempTxt.setText(items.get(position).getTempMax()+"°"+"/"+items.get(position).getTempMin()+"°");
        MainActivity.setImage(items.get(position).getIconPath(),holder.icon);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView dayTxt, tempTxt;
        ImageView icon;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            dayTxt = itemView.findViewById(R.id.cardViewDay);
            tempTxt = itemView.findViewById(R.id.cardViewTemperature);
            icon = itemView.findViewById(R.id.cardViewIcon);
        }
    }
}