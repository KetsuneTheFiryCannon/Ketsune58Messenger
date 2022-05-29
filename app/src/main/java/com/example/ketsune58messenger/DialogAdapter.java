package com.example.ketsune58messenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<ViewHolder2> {


    ArrayList<String> owners = new ArrayList<>();

    LayoutInflater inflater;

    public DialogAdapter(Context context, ArrayList<String> owners) {
        this.owners = owners;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.dialog_item, parent, false);

        return new ViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder2 holder, int position) {
//        String own = owner.get(position);
//        String msg = messages.get(position);
//        holder.mTextView.setText(msg);
//        holder.mOwnerView.setText(own);
    }

    @Override
    public int getItemCount() {
        return owners.size();
    }
}