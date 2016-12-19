package com.quirodev.usagestatsmanagersample;

import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public final class UsageStatsWrapper implements Comparable<UsageStatsWrapper> {

    private final UsageStats usageStats;
    private final Drawable appIcon;
    private final String appName;

    public UsageStatsWrapper(UsageStats usageStats, Drawable appIcon, String appName) {
        this.usageStats = usageStats;
        this.appIcon = appIcon;
        this.appName = appName;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public int compareTo(@NonNull UsageStatsWrapper usageStatsWrapper) {
        if (usageStats == null && usageStatsWrapper.getUsageStats() != null) {
            return 1;
        } else if (usageStatsWrapper.getUsageStats() == null && usageStats != null) {
            return -1;
        } else if (usageStatsWrapper.getUsageStats() == null && usageStats == null) {
            return 0;
        } else {
            return Long.compare(usageStatsWrapper.getUsageStats().getLastTimeUsed(),
                    usageStats.getLastTimeUsed());
        }
    }
}
