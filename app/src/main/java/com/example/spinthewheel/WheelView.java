package com.example.spinthewheel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class WheelView extends View {
	
	private Paint paint;
	private Paint textPaint;
	private RectF wheelRect;
	private float rotation = 0f;
	private String[] segments = {
		"Unlucky", "5% OFF", "Spin Again", "10% OFF",
		"Unlucky", "Free KitKat", "Unlucky", "20% OFF"
	};
	private int[] colors = {
		Color.parseColor("#FF4444"), // Red
		Color.parseColor("#4CAF50"), // Green
		Color.parseColor("#2196F3"), // Blue
		Color.parseColor("#FF9800"), // Orange
		Color.parseColor("#FF4444"), // Red
		Color.parseColor("#9C27B0"), // Purple
		Color.parseColor("#FF4444"), // Red
		Color.parseColor("#00BCD4")  // Cyan
	};

	// Highlight state
	private int highlightedSegmentIndex = -1;
	private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public WheelView(Context context) {
		super(context);
		init();
	}

	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(24f); // Increased from 16f to 24f for better visibility
		textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setShadowLayer(3f, 1f, 1f, Color.BLACK); // Add shadow for better contrast
		
		wheelRect = new RectF();
		
		// Highlight paint (translucent overlay)
		highlightPaint.setColor(Color.argb(120, 255, 255, 0)); // yellow with transparency
		highlightPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int size = Math.min(w, h);
		int padding = 20;
		wheelRect.set(padding, padding, size - padding, size - padding);
		
		// Adjust text size based on wheel size for better scaling
		float textSize = Math.max(18f, (wheelRect.width() * 0.08f));
		textPaint.setTextSize(textSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Save canvas state
		canvas.save();
		
		// Move to center and rotate
		canvas.translate(wheelRect.centerX(), wheelRect.centerY());
		canvas.rotate(rotation);
		canvas.translate(-wheelRect.centerX(), -wheelRect.centerY());
		
		// Draw wheel segments
		float sweepAngle = 360f / segments.length;
		float startAngle = -90f; // Start from top
		
		for (int i = 0; i < segments.length; i++) {
			// Draw segment
			paint.setColor(colors[i]);
			canvas.drawArc(wheelRect, startAngle, sweepAngle, true, paint);
			
			// Draw text with better positioning
			float textAngle = startAngle + sweepAngle / 2;
			float textRadius = wheelRect.width() * 0.4f; // Moved text closer to center for better fit
			float textX = wheelRect.centerX() + (float) Math.cos(Math.toRadians(textAngle)) * textRadius;
			float textY = wheelRect.centerY() + (float) Math.sin(Math.toRadians(textAngle)) * textRadius;
			
			// Rotate text to be readable
			canvas.save();
			canvas.translate(textX, textY);
			canvas.rotate(textAngle + 90);
			
			// Draw text with better positioning
			String text = segments[i];
			// Split longer text into multiple lines if needed
			if (text.contains(" ")) {
				String[] words = text.split(" ");
				float lineHeight = textPaint.getTextSize() + 4f;
				float totalHeight = lineHeight * words.length;
				float startYText = -totalHeight / 2f + lineHeight / 2f;
				
				for (int j = 0; j < words.length; j++) {
					canvas.drawText(words[j], 0, startYText + j * lineHeight, textPaint);
				}
			} else {
				canvas.drawText(text, 0, 0, textPaint);
			}
			
			canvas.restore();
			
			startAngle += sweepAngle;
		}
		
		// Highlight the selected segment (closest to top)
		if (highlightedSegmentIndex >= 0 && highlightedSegmentIndex < segments.length) {
			float highlightStart = -90f + highlightedSegmentIndex * sweepAngle;
			canvas.drawArc(wheelRect, highlightStart, sweepAngle, true, highlightPaint);
		}
		
		// Draw center circle
		paint.setColor(Color.parseColor("#FF6B35"));
		float centerRadius = wheelRect.width() * 0.15f;
		canvas.drawCircle(wheelRect.centerX(), wheelRect.centerY(), centerRadius, paint);
		
		// Draw inner center dot
		paint.setColor(Color.WHITE);
		float innerRadius = wheelRect.width() * 0.08f;
		canvas.drawCircle(wheelRect.centerX(), wheelRect.centerY(), innerRadius, paint);
		
		// Restore canvas state
		canvas.restore();
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
		invalidate();
	}

	public float getRotation() {
		return rotation;
	}

	public void setHighlightedSegmentIndex(int index) {
		this.highlightedSegmentIndex = index;
		invalidate();
	}
} 