package me.dennislysenko.directimageshare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
            Intent intent = getIntent();
            Context context = getApplicationContext();

            String subject = intent.getStringExtra("android.intent.extra.SUBJECT");
            String text = intent.getStringExtra("android.intent.extra.TEXT");

            URL url = null;
            try {
                url = new URL(subject);
            } catch (MalformedURLException e) {
                // no big
            }

            if (url == null) {
                try {
                    url = new URL(text);
                } catch (MalformedURLException e) {
                    Toast.makeText(context, "That is not a valid image file.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            Intent service = new Intent(context, ImageDownloadService.class);
            service.putExtra("url", "" + url);
            context.startService(service);

            Toast.makeText(context, "Starting download...", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
