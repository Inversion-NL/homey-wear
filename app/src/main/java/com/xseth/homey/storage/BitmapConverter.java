package com.xseth.homey.storage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class BitmapConverter {

    @TypeConverter
    public static Bitmap fromByteArray(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @TypeConverter
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if(bitmap == null)
            return new byte[0];

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
}
