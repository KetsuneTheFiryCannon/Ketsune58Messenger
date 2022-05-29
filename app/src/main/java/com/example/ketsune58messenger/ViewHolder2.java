package com.example.ketsune58messenger;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder2 extends RecyclerView.ViewHolder {

    String owner_name;
    Button dialog_button;

    public ViewHolder2(@NonNull View itemView) {
        super(itemView);

        dialog_button = itemView.findViewById(R.id.dialog_button);
        dialog_button.setText(owner_name);
    }
}
