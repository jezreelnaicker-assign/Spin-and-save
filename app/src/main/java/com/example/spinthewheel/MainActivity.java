package com.example.spinthewheel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    WheelView wheel;
    Button spinBtn;
    Button resetBtn;
    TextView resultText;
    TextView subtitleText;

    private final Random random = new Random();
    private boolean spinning = false;
    private boolean hasSpunToday = false;
    private String todayCode = "";
    private int spinsRemaining = 1; // Track remaining spins for "Spin Again"

    // 8 segments with the specified prizes
    private final String[] segments = {
            "Unlucky", "5% OFF", "Spin Again", "10% OFF",
            "Unlucky", "Free KitKat", "Unlucky", "20% OFF"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wheel = findViewById(R.id.wheelView);
        spinBtn = findViewById(R.id.spinBtn);
        resetBtn = findViewById(R.id.resetBtn);
        resultText = findViewById(R.id.resultText);
        subtitleText = findViewById(R.id.subtitleText);

        // Check if views are found
        if (wheel == null || spinBtn == null || resultText == null) {
            Toast.makeText(this, "Error: UI elements not found", Toast.LENGTH_LONG).show();
            return;
        }

        // TEMPORARY: Disable time limit for testing
        // checkIfSpunToday();
        
        // Initialize for testing (no time restrictions)
        initializeForTesting();

        spinBtn.setOnClickListener(v -> {
            if (!spinning && spinsRemaining > 0) {
                spinWheel();
            }
        });

        // Reset button for testing
        resetBtn.setOnClickListener(v -> {
            resetForTesting();
        });
    }

    private void checkIfSpunToday() {
        SharedPreferences prefs = getSharedPreferences("WheelPrefs", MODE_PRIVATE);
        String lastSpinDate = prefs.getString("lastSpinDate", "");
        String today = java.time.LocalDate.now().toString();
        
        if (today.equals(lastSpinDate)) {
            // User has already spun today
            hasSpunToday = true;
            todayCode = prefs.getString("todayCode", "");
            String wonPrize = prefs.getString("wonPrize", "");
            
            // Update UI to show already spun state
            updateUIForAlreadySpun(wonPrize);
        } else {
            // Reset spins for new day
            spinsRemaining = 1;
            // Clear any old data
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("lastSpinDate");
            editor.remove("todayCode");
            editor.remove("wonPrize");
            editor.apply();
        }
    }

    private void updateUIForAlreadySpun(String wonPrize) {
        subtitleText.setText("Come back tomorrow for another spin!");
        
        if ("Unlucky".equals(wonPrize)) {
            resultText.setText("No Prize");
        } else {
            resultText.setText("You won: " + wonPrize + "\nYour Code: " + todayCode);
        }
        
        spinBtn.setText("COME BACK TOMORROW");
        spinBtn.setEnabled(false);
        spinBtn.setAlpha(0.6f);
    }

    private void spinWheel() {
        if (wheel == null) {
            Toast.makeText(this, "Error: Wheel not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        spinning = true;
        spinBtn.setEnabled(false);
        spinBtn.setText("SPINNING...");

        // Spin by a random amount (5-8 full rotations + random offset)
        int fullRotations = random.nextInt(4) + 5; // 5-8 rotations
        int randomOffset = random.nextInt(360); // 0-359 degrees
        int targetRotation = (fullRotations * 360) + randomOffset;

        ValueAnimator animator = ValueAnimator.ofFloat(wheel.getRotation(), targetRotation);
        animator.setDuration(4000); // 4 seconds
        animator.setInterpolator(new DecelerateInterpolator(1.5f)); // Slow down at the end
        
        animator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            wheel.setRotation(value);
        });

        animator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                resultText.setText("Spinning...");
                wheel.setHighlightedSegmentIndex(-1);
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Compute the segment whose center is closest to the top (arrow tip)
                float finalRotation = wheel.getRotation() % 360f;
                if (finalRotation < 0) finalRotation += 360f;

                float segmentAngle = 360f / segments.length;
                // In WheelView we start drawing at -90 deg, so segment i center is at: -90 + i*segmentAngle + segmentAngle/2
                // The arrow tip is at top (north), which is -90 deg in canvas coordinates.
                // We need to offset for wheel rotation.
                float minAbs = Float.MAX_VALUE;
                int closestIndex = 0;
                for (int i = 0; i < segments.length; i++) {
                    float centerAngle = -90f + i * segmentAngle + (segmentAngle / 2f);
                    // Apply wheel rotation (wheel rotated by finalRotation clockwise)
                    float angleAtScreen = (centerAngle + finalRotation) % 360f;
                    if (angleAtScreen < -180f) angleAtScreen += 360f;
                    if (angleAtScreen > 180f) angleAtScreen -= 360f;
                    // We want closest to -90 (top)
                    float delta = angleAtScreen - (-90f);
                    // Normalize to [-180, 180]
                    if (delta > 180f) delta -= 360f;
                    if (delta < -180f) delta += 360f;
                    float absDelta = Math.abs(delta);
                    if (absDelta < minAbs) {
                        minAbs = absDelta;
                        closestIndex = i;
                    }
                }

                int segmentIndex = closestIndex;
                String selectedPrize = segments[segmentIndex];
                if (!"Unlucky".equals(selectedPrize) && !"Spin Again".equals(selectedPrize)) {
                    todayCode = generateRandomCode();
                }

                wheel.setHighlightedSegmentIndex(segmentIndex);

                // Handle outcomes
                if ("Spin Again".equals(selectedPrize)) {
                    // Award exactly one extra spin for landing on Spin Again
                    spinsRemaining += 1;
                    resultText.setText("Spin Again! You have " + spinsRemaining + " spin(s) left");
                    spinBtn.setText("SPIN AGAIN!");
                    spinBtn.setEnabled(true);
                    spinBtn.setAlpha(1.0f);
                    spinning = false;
                    Toast.makeText(MainActivity.this, "You get another spin!", Toast.LENGTH_SHORT).show();
                } else {
                    // Consume one spin for a non-Spin Again outcome
                    spinsRemaining -= 1;

                    if ("Unlucky".equals(selectedPrize)) {
                        resultText.setText("No Prize");
                        Toast.makeText(MainActivity.this, "Better luck next time!", Toast.LENGTH_SHORT).show();
                    } else {
                        String resultMessage = "You won: " + selectedPrize + "\nYour Code: " + todayCode;
                        resultText.setText(resultMessage);
                        String toastMessage;
                        switch (selectedPrize) {
                            case "Free KitKat":
                                toastMessage = "Congratulations! You won a free KitKat! Your code: " + todayCode;
                                break;
                            default:
                                toastMessage = "Congratulations! " + selectedPrize + " Your code: " + todayCode;
                                break;
                        }
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();
                    }

                    spinning = false;
                    if (spinsRemaining > 0) {
                        spinBtn.setText("SPIN AGAIN!");
                        spinBtn.setEnabled(true);
                        spinBtn.setAlpha(1.0f);
                    } else {
                        spinBtn.setText("SPIN COMPLETE");
                        spinBtn.setEnabled(false);
                        spinBtn.setAlpha(0.6f);
                    }
                }
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {}

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });

        animator.start();
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    private void saveSpinData(String wonPrize) {
        SharedPreferences prefs = getSharedPreferences("WheelPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastSpinDate", java.time.LocalDate.now().toString());
        editor.putString("todayCode", todayCode);
        editor.putString("wonPrize", wonPrize);
        editor.apply();
    }

    private void initializeForTesting() {
        // Reset all state for testing
        hasSpunToday = false;
        spinsRemaining = 1; // Start with 1 spin
        todayCode = "";
        
        // Set initial UI state
        resultText.setText("Ready to spin?");
        subtitleText.setText("Try your luck and win amazing prizes!");
        spinBtn.setText("SPIN THE WHEEL!");
        spinBtn.setEnabled(true);
        spinBtn.setAlpha(1.0f);
    }

    private void resetForTesting() {
        // Reset all state for testing
        hasSpunToday = false;
        spinsRemaining = 1; // Reset to 1 spin
        todayCode = "";
        
        // Reset UI elements
        resultText.setText("Ready to spin?");
        subtitleText.setText("Try your luck and win amazing prizes!");
        spinBtn.setText("SPIN THE WHEEL!");
        spinBtn.setEnabled(true);
        spinBtn.setAlpha(1.0f);
        
        // Reset wheel rotation
        if (wheel != null) {
            wheel.setRotation(0f);
            wheel.setHighlightedSegmentIndex(-1);
        }
        
        Toast.makeText(this, "Reset for testing - Ready to spin!", Toast.LENGTH_SHORT).show();
    }

    private void clearStoredData() {
        SharedPreferences prefs = getSharedPreferences("WheelPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        
        // Reset UI state
        hasSpunToday = false;
        spinsRemaining = 1;
        todayCode = "";
        
        // Reset UI elements
        resultText.setText("Ready to spin?");
        subtitleText.setText("Try your luck and win amazing prizes!");
        spinBtn.setText("SPIN THE WHEEL!");
        spinBtn.setEnabled(true);
        spinBtn.setAlpha(1.0f);
        
        Toast.makeText(this, "App reset for testing", Toast.LENGTH_SHORT).show();
    }

    private void highlightWinningSegment(int segmentIndex) {
        // Add a brief flash effect to highlight the winning segment
        // This will be handled by the wheel view itself
        if (wheel != null) {
            // Force a redraw to show the winning segment more clearly
            wheel.invalidate();
            
            // Add a brief vibration feedback (if available)
            try {
                android.os.Vibrator vibrator = (android.os.Vibrator) getSystemService(android.content.Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(200); // 200ms vibration
                }
            } catch (Exception e) {
                // Ignore if vibration is not available
            }
        }
    }
}
