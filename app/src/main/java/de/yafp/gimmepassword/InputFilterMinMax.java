package de.yafp.gimmepassword;

import android.text.InputFilter;
import android.text.Spanned;

class InputFilterMinMax implements InputFilter {
    private final int min, max;

// --Commented out by Inspection START (05.04.18 14:45):
//    public InputFilterMinMax(int min, int max) {
//        this.min = min;
//        this.max = max;
//    }
// --Commented out by Inspection STOP (05.04.18 14:45)

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
            //
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
