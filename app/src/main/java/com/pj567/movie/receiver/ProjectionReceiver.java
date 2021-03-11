package com.pj567.movie.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pj567.movie.ui.activity.PraseActivity;
import com.pj567.movie.ui.activity.ProjectionPlayActivity;
import com.pj567.movie.util.AppManager;

/**
 * @author pj567
 * @date :2021/3/5
 * @description:
 */
public class ProjectionReceiver extends BroadcastReceiver {
    public static String action = "android.content.movie.projection.Action";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (action.equals(intent.getAction()) && intent.getExtras() != null) {
            if (AppManager.getInstance().getActivity(PraseActivity.class) != null) {
                AppManager.getInstance().backActivity(PraseActivity.class);
                AppManager.getInstance().finishActivity(PraseActivity.class);
            }
            AppManager.getInstance().finishActivity(ProjectionPlayActivity.class);
            Intent newIntent = new Intent(context, PraseActivity.class);
            newIntent.putExtra("html", intent.getExtras().getString("html"));
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(newIntent);
        }
    }
}