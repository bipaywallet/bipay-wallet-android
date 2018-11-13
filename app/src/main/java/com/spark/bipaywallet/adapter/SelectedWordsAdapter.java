package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.spark.bipaywallet.R;

import java.util.List;

public class SelectedWordsAdapter extends RecyclerView.Adapter<SelectedWordsAdapter.MyHolder> {
    private Context context;
    private List<String> list;

    private AdapterView.OnItemClickListener itemClickListener;

    public SelectedWordsAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = li.inflate(R.layout.item_grid_memory_select, null);
        convertView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new MyHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.tvWords.setText(list.get(position));

        holder.tvWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(null, null, position, 0);

//                    holder.tvWords.setSelected(true);
//                    holder.tvWords.setClickable(false);
                }
            }
        });

//        if (holder.tvWords.isSelected()) {
//            holder.tvWords.setSelected(false);
//            holder.tvWords.setClickable(true);
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tvWords;

        public MyHolder(View itemView) {
            super(itemView);
            tvWords = itemView.findViewById(R.id.tvWords);
        }
    }

    public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
