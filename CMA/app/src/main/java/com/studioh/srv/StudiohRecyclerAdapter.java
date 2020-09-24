package com.studioh.srv;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.naa.data.Dson;
import com.naa.data.Utility;

public class StudiohRecyclerAdapter extends RecyclerView.Adapter<StudiohViewHolder> {
    Dson dson;
    int rlayout;
    public StudiohRecyclerAdapter(Dson dson, int rlayout){
        this.dson = dson;;
        this.rlayout = rlayout;
    }
    public Dson getItem(){
        return dson;
    }
    @Override
    public final int getItemViewType(int position) {
        return position;
    }
    @Override
    public StudiohViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        StudiohViewHolder nikitaViewHolder = StudiohViewHolder.getInstance(viewGroup, rlayout);
        if (onitemClickListener!=null){
            nikitaViewHolder.itemView.setTag(String.valueOf(position));
            nikitaViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (onitemClickListener!=null){
                        onitemClickListener.onItemClick(dson, v , Utility.getInt(String .valueOf(v.getTag())));
                    }
                }
            });
        }
        return nikitaViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull StudiohViewHolder viewHolder, int position) {

    }
    @Override
    public int getItemCount() {
        return dson.size();
    }
    private OnItemClickListener onitemClickListener;
    public StudiohRecyclerAdapter setOnitemClickListener(OnItemClickListener onitemClickListener){
        this.onitemClickListener=onitemClickListener;
        return this;
    }

    public interface OnItemClickListener {
        void onItemClick(Dson parent, View view, int position);
    }
}
