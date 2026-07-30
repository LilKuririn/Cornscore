package com.scoretosslabs.cornscore;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Le peu que la page ne peut pas faire seule dans une WebView : écrire un
 * fichier dans les Téléchargements, et ouvrir le partage du téléphone.
 * Seules les pages servies depuis les assets sont chargées, ce pont n'est
 * donc jamais exposé à du contenu extérieur.
 */
public class Bridge {

    private final Activity activity;

    Bridge(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void saveFile(final String name, final String content) {
        activity.runOnUiThread(new Runnable() {
            @Override public void run() { write(name, content); }
        });
    }

    @JavascriptInterface
    public void share(final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_TEXT, text);
                activity.startActivity(Intent.createChooser(send, activity.getString(R.string.share_via)));
            }
        });
    }

    private void write(String name, String content) {
        String safe = (name == null || name.isEmpty()) ? "cornscore.json" : name;
        byte[] bytes = content.getBytes(Charset.forName("UTF-8"));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, safe);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/json");
                values.put(MediaStore.Downloads.IS_PENDING, 1);
                Uri item = activity.getContentResolver()
                        .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (item == null) throw new IllegalStateException("insert refusé");
                OutputStream out = activity.getContentResolver().openOutputStream(item);
                out.write(bytes);
                out.close();
                values.clear();
                values.put(MediaStore.Downloads.IS_PENDING, 0);
                activity.getContentResolver().update(item, values, null, null);
            } else {
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!dir.exists()) dir.mkdirs();
                FileOutputStream out = new FileOutputStream(new File(dir, safe));
                out.write(bytes);
                out.close();
            }
            Toast.makeText(activity, activity.getString(R.string.saved_to, safe), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(activity, R.string.save_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
