package com.xseth.homey.utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * Class for updating background color of views
 */
public class ColorRunner extends AsyncTask<View, Integer, Void> {

    // Logger TAG
    public static final String TAG = "ColorRunner";
    // Amount of time in MS to wait between color switches
    public static final int SLEEP = 100;
    // Resolution of color possibilities
    public static final int COLOR_SIZE = 500;
    // Array depicting rainbow colors
    public static int[] COLORS = new int[COLOR_SIZE];
    // View for which background is updated
    private View view;

    public ColorRunner(View v){
        view = v;

        // Generate colors
        double jump = 360.0 / (COLOR_SIZE*1.0);
        for (int i = 0; i < COLORS.length; i++) {
            COLORS[i] = Color.HSVToColor(new float[]{(float) (jump*i), 1.0f, 1.0f});
        }
    }

    protected void onProgressUpdate(Integer... colors) {
        Log.v(TAG, "Setting color: "+colors[0]);
        view.setBackgroundColor(colors[0]);
    }

    @Override
    protected Void doInBackground(View... views) {
        int index = new Random().nextInt(COLOR_SIZE-1);
        int color;
        int max = COLORS.length;

        Log.d(TAG, "Starting run");

        while(true){
            color = COLORS[index];
            publishProgress(color);

            index += 1;

            if(index == max)
                index = 0;

            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.v(TAG, "onPostExecute");
    }
}
