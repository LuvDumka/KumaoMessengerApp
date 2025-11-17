package com.example.mentorconnect.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

/**
 * Generates default avatar images based on the first letter of a name
 */
object AvatarGenerator {
    
    private val backgroundColors = listOf(
        "#FF6B6B", // Red
        "#4ECDC4", // Teal
        "#45B7D1", // Blue
        "#FFA07A", // Orange
        "#98D8C8", // Mint
        "#F7DC6F", // Yellow
        "#BB8FCE", // Purple
        "#85C1E2", // Sky Blue
        "#F8B88B", // Peach
        "#52B788", // Green
        "#E76F51", // Burnt Orange
        "#2A9D8F", // Dark Teal
        "#E9C46A", // Gold
        "#F4A261", // Coral
        "#8E7CC3", // Lavender
        "#6C757D", // Gray
        "#FF8C94", // Pink
        "#A8DADC", // Light Blue
        "#457B9D", // Steel Blue
        "#F1FAEE"  // White Blue
    )
    
    /**
     * Generates a drawable with the first letter of the name
     * @param name The name to generate avatar from
     * @param context Context for resources
     * @param size Size of the avatar in pixels (default 200)
     * @return Drawable with colored background and initial letter
     */
    fun generateAvatar(name: String, context: Context, size: Int = 200): Drawable {
        val initial = getInitial(name)
        val backgroundColor = getColorForName(name)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Draw background
        val backgroundPaint = Paint().apply {
            color = Color.parseColor(backgroundColor)
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)
        
        // Draw text
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = size * 0.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        
        val textY = (size / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(initial, size / 2f, textY, textPaint)
        
        return BitmapDrawable(context.resources, bitmap)
    }
    
    /**
     * Get the first letter from the name
     */
    private fun getInitial(name: String): String {
        return name.trim().firstOrNull()?.uppercase() ?: "?"
    }
    
    /**
     * Get a consistent color for a name (same name always gets same color)
     */
    private fun getColorForName(name: String): String {
        val index = name.hashCode().let { 
            if (it < 0) -it else it 
        } % backgroundColors.size
        return backgroundColors[index]
    }
}
