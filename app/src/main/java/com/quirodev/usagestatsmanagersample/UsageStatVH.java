package com.quirodev.usagestatsmanagersample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
            lastTimeUsed.setText(R.string.last_time_used_never);
        }else if (usageStatsWrapper.getUsageStats().getLastTimeUsed() == 0L){
            lastTimeUsed.setText(R.string.last_time_used_never);
        } else{
            lastTimeUsed.setText(App.getApp().getString(R.string.last_time_used,
                    DateUtils.format(usageStatsWrapper)));
        }
    }
}
