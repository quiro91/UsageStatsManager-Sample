package com.quirodev.usagestatsmanagersample;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.os.Process.myUid;

public class UsagePresenter implements UsageContract.Presenter {

    private static final int flags = PackageManager.GET_META_DATA |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_UNINSTALLED_PACKAGES;

    private UsageStatsManager usageStatsManager;
    private PackageManager packageManager;
    private UsageContract.View view;
    private final Context context;

    public UsagePresenter(Context context, UsageContract.View view) {
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        packageManager = context.getPackageManager();
        this.view = view;
        this.context = context;
    }

    private Observable<List<String>> appInstalled = Observable.fromCallable(() -> packageManager.getInstalledApplications(flags))
            .flatMap(Observable::from)
            .map(it -> it.packageName)
            .filter(this::isUserApp)
            .subscribeOn(Schedulers.computation())
            .toList();

    private Observable<List<UsageStats>> usageStats = Observable.fromCallable(() -> usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start(), System.currentTimeMillis())).flatMap(Observable::from)
            .groupBy(UsageStats::getPackageName)
            .flatMap(Observable::toList)
            .map(this::getMostRecentFromList)
            .filter(it -> it != null)
            .subscribeOn(Schedulers.computation())
            .toList();

    private long start() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTimeInMillis();
    }

    @Override
    public void retrieveUsageStats() {
        if (!checkForPermission(context)) {
            view.onUserHasNoPermission();
            return;
        }

        appInstalled.zipWith(usageStats, this::buildUsageStatsWrapper)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::onUsageStatsRetrieved, error -> Log.e("", error.getMessage()));
    }

    private boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    private List<UsageStatsWrapper> buildUsageStatsWrapper(List<String> packageNames, List<UsageStats> usageStatses) {
        List<UsageStatsWrapper> list = new ArrayList<>();
        for (String name : packageNames) {
            boolean added = false;
            for (UsageStats stat : usageStatses) {
                if (name.equals(stat.getPackageName())) {
                    added = true;
                    list.add(fromUsageStat(stat));
                }
            }
            if (!added) {
                list.add(fromUsageStat(name));
            }
        }
        Collections.sort(list);
        return list;
    }

    private UsageStats getMostRecentFromList(List<UsageStats> usageStatsList) {
        if (usageStatsList.isEmpty()) {
            return null;
        }
        UsageStats selected = usageStatsList.get(0);
        for (UsageStats stat : usageStatsList) {
            if (selected.getLastTimeUsed() < stat.getLastTimeUsed()) {
                selected = stat;
            }
        }
        return selected;
    }

    private boolean isUserApp(String packageName) throws IllegalArgumentException {
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        return false;
    }

    private UsageStatsWrapper fromUsageStat(String packageName) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, 0);
            return new UsageStatsWrapper(null, packageManager.getApplicationIcon(ai), packageManager.getApplicationLabel(ai).toString());

        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private UsageStatsWrapper fromUsageStat(UsageStats usageStats) throws IllegalArgumentException {
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(usageStats.getPackageName(), 0);
            return new UsageStatsWrapper(usageStats, packageManager.getApplicationIcon(ai), packageManager.getApplicationLabel(ai).toString());

        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
