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
import com.spark.bipaywallet.entity.Wallet;

import java.util.List;

public class HomeSideAdapter extends RecyclerView.Adapter<HomeSideAdapter.MyHolder> {
    private Context context;
    private List<Wallet> list;
    private int selectItem = -1;

    private AdapterView.OnItemClickListener itemClickListener;

    public HomeSideAdapter(Context context, List<Wallet> walletList) {
        this.context = context;
        this.list = walletList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = li.inflate(R.layout.item_side, null);
        convertView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new MyHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        Wallet wallet = list.get(position);

        holder.tvName.setText(wallet.getName());

        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(null, null, position, 0);
                    setAdapterPos(position);
                }
            }
        });

        if (position == selectItem) {
            holder.tvName.setSelected(true);
        } else {
            holder.tvName.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public MyHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setAdapterPos(int pos) {
        int lastPos = selectItem;
        selectItem = pos;
        notifyItemChanged(pos);
        notifyItemChanged(lastPos);
    }

    public int getCurPos() {
        return selectItem;
    }
}
