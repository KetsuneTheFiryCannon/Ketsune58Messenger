package com.example.ketsune58messenger;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView mTextView;
    TextView mOwnerView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.message_output_text);
        mOwnerView = itemView.findViewById(R.id.message_output_owner);
    }
}