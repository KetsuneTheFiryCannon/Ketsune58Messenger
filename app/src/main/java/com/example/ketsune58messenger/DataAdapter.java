package com.example.ketsune58messenger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> owner = new ArrayList<>();

    LayoutInflater inflater;

    public DataAdapter(Context context, ArrayList<String> messages, ArrayList<String> owner) {
        this.owner = owner;
        this.messages = messages;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String msg = messages.get(position);
        String own = owner.get(position);
        holder.mTextView.setText(msg);
        holder.mOwnerView.setText(own);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}