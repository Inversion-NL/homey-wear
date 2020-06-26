package com.xseth.homey.utils;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * Class for updating background color of views
 */
public class ColorRunner {

    // Logger TAG
    public static final String TAG = "ColorRunner";
    // Amount of time in MS to wait between color switches
    public static final int SLEEP = 500;
    // Resolution of color possibilities
    public static final int COLOR_SIZE = 500;
    // Array depicting rainbow colors
    public static int[] COLORS = new int[COLOR_SIZE];

    /**
     * Start the ColorRunner, thread which changes background color
     * @param view View for which background color is updated
     */
    public static void startColorRunner(View view){
        double jump = 360.0 / (COLOR_SIZE*1.0);
        for (int i = 0; i < COLORS.length; i++) {
            COLORS[i] = Color.HSVToColor(new float[]{(float) (jump*i), 1.0f, 1.0f});
        }

        // Change color in background
        new Thread(() -> {
            int color;
            boolean run = true;
            int max = COLORS.length;
            int index = new Random().nextInt(COLOR_SIZE-1);

            Log.d(TAG, "Starting colorRun");

            do {
                color = COLORS[index];
                view.setBackgroundColor(color);

                index += 1;

                if (index == max)
                    index = 0;

                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException ignored) { run = false; }
            } while (run);
        }).start();
    }
}