package com.m5.android.avicola.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.m5.android.avicola.DetailActivity;
import com.m5.android.avicola.SettingsActivity;
import com.m5.android.avicola.WebActivity;
import com.m5.android.avicola.app.Constants;
import com.m5.android.avicola.model.Content;

public class IntentUtil {

    public static void startSettingsForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, SettingsActivity.class), requestCode);
    }

    public static void startDetailActivity(Context context, Content item) {
        final Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Constants.EXTRAS_CONTENT, item.toBundle());
        context.startActivity(intent);
    }

    public static void startWebActivity(Context context, String url) {
        final Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(Constants.EXTRAS_URL, url);
        context.startActivity(intent);
    }

    public static Intent getShareIntent(String link) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    public static void browser(Context context, String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        context.startActivity(i);
    }
}
