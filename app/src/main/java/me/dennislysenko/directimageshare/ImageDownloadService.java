package me.dennislysenko.directimageshare;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by dennis on 1/3/15.
 */
public class ImageDownloadService extends IntentService {
    private static final String IMAGE_CACHE_FILENAME = "image.png";
    private static final int MAX_WIDTH = 3000;
    private static final int MAX_HEIGHT = 3000;

    /**
     * Creates an ImageDownloadService
     */
    public ImageDownloadService() {
        super("ImageDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url;
        if ((url = (String)intent.getSerializableExtra("url")) != null) {
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    File file = new File(Environment.getExternalStorageDirectory(), IMAGE_CACHE_FILENAME);

                    try {
                        FileOutputStream stream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ImageDownloadService.this,
                                "Couldn't save image: error compressing image.",
                                Toast.LENGTH_LONG).show();
                    }

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);

                    sendIntent.setType("image/png");
                    sendIntent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse(file.toURI().toString()));
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(sendIntent);
                }
            },
            MAX_WIDTH, MAX_HEIGHT,
            Bitmap.Config.ARGB_8888,
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    Toast.makeText(ImageDownloadService.this,
                            "Couldn't save image: error downloading image.",
                            Toast.LENGTH_LONG).show();
                }
            }));
            queue.start();
        }
    }
}
