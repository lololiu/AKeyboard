package xyz.royliu.library;

import android.graphics.drawable.Drawable;

/**
 * Created by liulou on 2017/4/11.
 * desc:
 */

public class AKeyStyle {
    private Drawable keyBackground;
    private float keyTextSize;
    private int keyTextColor;
    private String keyLabel;

    public AKeyStyle() {
    }

    public AKeyStyle(Drawable keyBackground, float keyTextSize, int keyTextColor, String keyLabel) {
        this.keyBackground = keyBackground;
        this.keyTextSize = keyTextSize;
        this.keyTextColor = keyTextColor;
        this.keyLabel = keyLabel;
    }

    public Drawable getKeyBackground() {
        return keyBackground;
    }

    public void setKeyBackground(Drawable keyBackground) {
        this.keyBackground = keyBackground;
    }

    public float getKeyTextSize() {
        return keyTextSize;
    }

    public void setKeyTextSize(float keyTextSize) {
        this.keyTextSize = keyTextSize;
    }

    public int getKeyTextColor() {
        return keyTextColor;
    }

    public void setKeyTextColor(int keyTextColor) {
        this.keyTextColor = keyTextColor;
    }

    public String getKeyLabel() {
        return keyLabel;
    }

    public void setKeyLabel(String keyLabel) {
        this.keyLabel = keyLabel;
    }
}
