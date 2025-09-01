package com.LuvDumka.kumaonmessenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class AvatarHelper {
    
    private static final int[] BACKGROUND_COLORS = {
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#2196F3"), // Blue
        Color.parseColor("#FF9800"), // Orange
        Color.parseColor("#9C27B0"), // Purple
        Color.parseColor("#F44336"), // Red
        Color.parseColor("#607D8B"), // Blue Grey
        Color.parseColor("#795548"), // Brown
        Color.parseColor("#009688")  // Teal
    };
    
    public static Drawable generateLetterAvatar(Context context, String name, int size) {
        if (name == null || name.isEmpty()) {
            name = "?";
        }
        
        // Get first letter
        String letter = name.substring(0, 1).toUpperCase();
        
        // Choose background color based on first letter
        int colorIndex = Math.abs(letter.hashCode()) % BACKGROUND_COLORS.length;
        int backgroundColor = BACKGROUND_COLORS[colorIndex];
        
        // Create bitmap
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Draw background circle
        Paint backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundColor);
        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, backgroundPaint);
        
        // Draw letter
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(size * 0.4f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        // Center the text
        Rect textBounds = new Rect();
        textPaint.getTextBounds(letter, 0, letter.length(), textBounds);
        float x = size / 2f;
        float y = size / 2f + textBounds.height() / 2f;
        
        canvas.drawText(letter, x, y, textPaint);
        
        return new BitmapDrawable(context.getResources(), bitmap);
    }
    
    public static int getAvatarSize(Context context) {
        // Convert 40dp to pixels (same as in layout)
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (40 * density);
    }
}
