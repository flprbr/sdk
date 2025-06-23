package com.stela.sdkrfidtest;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private final List<String> logList = new ArrayList<>();

    public void addLog(String message) {
        logList.add(message);
        notifyItemInserted(logList.size() - 1);
    }

    public void clearLog() {
        logList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setTextSize(14);
        tv.setTextColor(Color.BLACK);
        return new LogViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        ((TextView) holder.itemView).setText(logList.get(position));
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
