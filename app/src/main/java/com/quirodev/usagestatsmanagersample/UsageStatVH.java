package com.quirodev.usagestatsmanagersample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class UsageStatVH extends RecyclerView.ViewHolder {

    private ImageView appIcon;
    private TextView appName;
    private TextView lastTimeUsed;

    public UsageStatVH(View itemView) {
        super(itemView);

        appIcon = (ImageView) itemView.findViewById(R.id.icon);
        appName = (TextView) itemView.findViewById(R.id.title);
        lastTimeUsed = (TextView) itemView.findViewById(R.id.last_used);
    }

    public void bindTo(UsageStatsWrapper usageStatsWrapper) {
        appIcon.setImageDrawable(usageStatsWrapper.getAppIcon());
        appName.setText(usageStatsWrapper.getAppName());
        if (usageStatsWrapper.getUsageStats() == null){
            lastTimeUsed.setText("Last time used: never");
        }else{
            lastTimeUsed.setText("Last time used: " + format(usageStatsWrapper.getUsageStats().getLastTimeUsed()));
        }

    }

    private String format(long millis) {
        if(millis <= 0L){
            return "never";
        }
        DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
        return format.format(millis);
    }
}
