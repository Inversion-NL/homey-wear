package com.xseth.homey.utils;

import android.graphics.Color;
import android.view.View;

import java.util.Random;

import timber.log.Timber;

/**
 * Class for updating background color of views
 */
public class ColorRunner implements Runnable{

    // Thread object
    private static ColorRunner INSTANCE;
    // Boolean used to start/stop runnable
    private boolean run = true;
    // Boolean used to pause/unpause runnable
    private boolean pause = false;
    // Amount of time in MS to wait between color switches
    public final int SLEEP = 500;
    // Resolution of color possibilities
    public final int COLOR_SIZE = 250;
    // Array depicting rainbow colors
    public int[] COLORS = new int[COLOR_SIZE];
    // View object to colorize
    private View view;

    /**
     * Start the ColorRunner, thread which changes background color
     * @param view View for which background color is updated
     */
    public static void startColorRunner(View view){
        // Change color in background
        INSTANCE = new ColorRunner(view);
        new Thread(INSTANCE).start();
    }

    /**
     * Resume paused ColorRunner Thread
     */
    public static void resumeColorRunner(){
        INSTANCE.pause = false;
    }

    /**
     * Pause running ColorRunner Thread
     */
    public static void pauseColorRunner(){
        INSTANCE.pause = true;
    }

    /**
     * Stop ColorRunner Thread
     */
    public static void stopColorRunner(){
        INSTANCE.run = false;
    }

    private ColorRunner(View view){
        this.view = view;

        double jump = 360.0 / (COLOR_SIZE*1.0);
        for (int i = 0; i < COLORS.length; i++) {
            COLORS[i] = Color.HSVToColor(new float[]{(float) (jump*i), 1.0f, 1.0f});
        }
    }

    @Override
    public void run() {
        int color;

        int max = COLORS.length;
        int index = new Random().nextInt(COLOR_SIZE-1);

        Timber.d("Starting colorRun");

        do {
            // pause remain in sleep forever until unpause
            while(pause){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }

            color = COLORS[index];
            view.setBackgroundColor(color);

            index += 1;

            if (index == max)
                index = 0;

            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException ignored) { run = false; }
        } while (run);
    }
}