package com.studioh.srv;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StudiohViewHolder extends RecyclerView.ViewHolder {
    public View v ;
    public StudiohViewHolder(@NonNull View itemView) {
        super(itemView);
        v = itemView;
    }
    public static StudiohViewHolder getInstance(ViewGroup viewParent, int view){
        View v = LayoutInflater.from(viewParent.getContext()).inflate(view, viewParent, false);
        return new StudiohViewHolder(v);
    }
    public <T extends View> T find(int id) {
        return (T) v.findViewById(id);
    }
    public <T extends View> T find(int id, Class<? super T> s) {
        return (T) v.findViewById(id);
    }
    public <T extends View> T findView(View v, int id, Class<? super T> s) {
        return (T) v.findViewById(id);
    }
}
