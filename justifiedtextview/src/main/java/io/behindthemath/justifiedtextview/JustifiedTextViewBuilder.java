package io.behindthemath.justifiedtextview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import static io.behindthemath.justifiedtextview.JustifiedTextView.DEFAULT_WORD_SPACING_BASE_PERCENTAGE;

/**
 * This class is a Builder pattern for the {@link JustifiedTextView} class.
 * <p>
 * &copy; 2016 BehindTheMath (<a href=behindthemath.io>behindthemath.io</a>).
 * <p>
 * License: <a href=http://www.apache.org/licenses/LICENSE-2.0.html>Apache License 2.0</a>
 */
public class JustifiedTextViewBuilder {
    private Context mContext;

    private String mText = "";
    @StringRes private int mTextResId = 0;

    @ColorInt private int mTextColor = Color.BLACK;
    @ColorRes private int mTextColorResId = 0;

    private float mTextSize = 0;
    private String mTextSizeDimen = null;

    @FloatRange(from = 1f) private float mLineSpacingMultiplier = 1.3f;

    private float mLineSpacingExtra = 0;
    private String mLineSpacingExtraDimen = null;

    private float mLineHeight = 0;

    @FloatRange(from = 0, to = 1f) private float mMinimumWordSpacingBasePercentage = 0;
    @Nullable private String mMinimumWordSpacingBaseCharacter = null;
    @FloatRange(from = 0) private float mMinimumWordSpacing = 0;

    private boolean mForceLastLineJustification = false;

    public JustifiedTextViewBuilder(Context context){
        mContext = context;
    }

    public JustifiedTextViewBuilder setText(@NonNull String text) {
        if (text != null) {
            mText = text;
            return this;
        } else {
            throw new IllegalArgumentException("Text cannot be null.");
        }
    }

    /**
     * Sets the text to be displayed by the view, from a string resource.
     *
     * @param textResId The resource ID of a string to set as the text to be displayed by the view.
     *
     * @throws IllegalArgumentException If {@code textResId} equals 0.
     */
    public JustifiedTextViewBuilder setText(@StringRes int textResId) {
        if (textResId != 0) {
            mTextResId = textResId;
            return this;
        } else {
            throw new IllegalArgumentException("textResId must be set to a valid string resource ID.");
        }
    }

    /**
     * Sets the text color.
     *
     * @param textColor A color, encoded as 0xAARRGGBB.
     */
    public JustifiedTextViewBuilder setTextColor(@ColorInt int textColor) {
        mTextColor = textColor;
        return this;
    }

    /**
     * Sets the text color, based on a color resource.
     *
     * @param textColorResId A color resource ID.
     */
    public JustifiedTextViewBuilder setTextColorRes(@ColorRes int textColorResId) {
        if (textColorResId != 0) {
            mTextColorResId = textColorResId;
            return this;
        } else {
            throw new IllegalArgumentException("textColorResId must be set to a valid color resource ID.");
        }
    }

    /**
     * Sets the text size, in pixels.
     *
     * @param textSize The text size, in pixels.
     *
     * @throws IllegalArgumentException If {@code textSize} is not more than 0.
     */
    public JustifiedTextViewBuilder setTextSize(@FloatRange(from = 0, fromInclusive = false) float textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
            return this;
        } else {
            throw new IllegalArgumentException("textSize must be more than 0.");
        }
    }

    /**
     * Sets the text size, as a dimension.
     *
     * @param textSizeDimen The text size, as a dimension string.
     *
     * @throws IllegalArgumentException If {@code textSizeDimen} is {@code null}.
     */
    public JustifiedTextViewBuilder setTextSize(@NonNull @Size(min = 1) String textSizeDimen) {
        if (textSizeDimen != null && textSizeDimen.length() != 0) {
            mTextSizeDimen = textSizeDimen;
            return this;
        } else {
            throw new IllegalArgumentException("textSizeDimen cannot be null.");
        }
    }

    /**
     * Sets the extra line spacing to be added after the base line spacing is calculated, in pixels.
     *
     * @param lineSpacingExtra The extra line spacing, in pixels.
     *
     * @throws IllegalArgumentException If {@code lineSpacingExtra} is less than 0.
     */
    public JustifiedTextViewBuilder setLineSpacingExtra(@FloatRange(from = 0) float lineSpacingExtra) {
        if (lineSpacingExtra >= 0) {
            mLineSpacingExtra = lineSpacingExtra;
            return this;
        } else {
            throw new IllegalArgumentException("lineSpacingExtra cannot be less than 0.");
        }
    }

    /**
     * Sets the extra line spacing to be added after the base line spacing is calculated, as a dimension.
     *
     * @param lineSpacingExtraDimen The extra line spacing, as a dimension string.
     *
     * @throws IllegalArgumentException If {@code lineSpacingExtraDimen} is {@code null}.
     */
    public JustifiedTextViewBuilder setLineSpacingExtra(@NonNull @Size(min = 1) String lineSpacingExtraDimen) {
        if (lineSpacingExtraDimen != null && lineSpacingExtraDimen.length() != 0) {
            mLineSpacingExtraDimen = lineSpacingExtraDimen;
            return this;
        } else {
            throw new IllegalArgumentException("lineSpacingExtraDimen must be set to a valid dimension");
        }
    }

    /**
     * Sets the line spacing multiplier.
     *
     * @param lineSpacingMultiplier The line spacing multiplier. Must equal at least 1.0.
     *
     * @throws IllegalArgumentException If {@code lineSpacingMultiplier} is less than 1.0.
     */
    public JustifiedTextViewBuilder setLineSpacingMultiplier(@FloatRange(from = 1.0) float lineSpacingMultiplier) {
        if (lineSpacingMultiplier >= 1f) {
            mLineSpacingMultiplier = lineSpacingMultiplier;
            return this;
        } else {
            throw new IllegalArgumentException("lineSpacingMultiplier must be at least 1.0.");
        }
    }

    /**
     * Sets the minimum word spacing.
     *
     * @param minimumWordSpacing The minimum word spacing. Must be at least 0.
     *
     * @throws IllegalArgumentException If {@code minimumWordSpacing} is not more than 0.
     */
    public JustifiedTextViewBuilder setMinimumWordSpacing(@FloatRange(from = 0) float minimumWordSpacing) {
        if (minimumWordSpacing > 0) {
            mMinimumWordSpacing = minimumWordSpacing;
            return this;
        } else {
            throw new IllegalArgumentException("minimumWrdSpacing must be more than 0.");
        }
    }

    /**
     * Sets the minimum word spacing, based on a character, with a default percentage of {@code DEFAULT_WORD_SPACING_BASE_PERCENTAGE} (0.8f).
     *
     * @param minimumWordSpacingBaseCharacter A character to base the minimum word spacing on.
     */
    public JustifiedTextViewBuilder setMinimumWordSpacing(@NonNull @Size(1) String minimumWordSpacingBaseCharacter) {
        if (minimumWordSpacingBaseCharacter != null && minimumWordSpacingBaseCharacter.length() >= 1 && minimumWordSpacingBaseCharacter.length() <= 1) {
            setMinimumWordSpacing(minimumWordSpacingBaseCharacter, DEFAULT_WORD_SPACING_BASE_PERCENTAGE);
            return this;
        } else {
            throw new IllegalArgumentException("setMinimumWordSpacing(String) must be set to a single character to be used as the base for the word spacing.");
        }
    }

    /**
     * Sets the minimum word spacing, based on a character and a percentage.
     *
     * @param minimumWordSpacingBaseCharacter A character to base the minimum word spacing on.
     * @param minimumWordSpacingBasePercentage The percentage of the width of the character to use as the minimum word spacing.  Must be between 0 and 1.0. If 0, DEFAULT_WORD_SPACING_BASE_PERCENTAGE (0.8f) will be used.
     *
     * @throws IllegalArgumentException If {@code minimumWordSpacingBaseCharacter} is null, or if {@code minimumWordSpacingBasePercentage} is not between 0 and 1.0.
     */
    public JustifiedTextViewBuilder setMinimumWordSpacing(@NonNull @Size(1) String minimumWordSpacingBaseCharacter, @FloatRange(from = 0, to = 1f, fromInclusive = false) float minimumWordSpacingBasePercentage) {
        if (minimumWordSpacingBaseCharacter == null || minimumWordSpacingBaseCharacter.length() < 1 || minimumWordSpacingBaseCharacter.length() > 1) {
            throw new IllegalArgumentException("minimumWordSpacingBaseCharacter must be set to a single character to be used as the base for the word spacing.");
        } else if (minimumWordSpacingBasePercentage <= 0 || minimumWordSpacingBasePercentage > 1f) {
            throw new IllegalArgumentException("minimumWordSpacingBasePercentage must be > 0 and <= 1.0.");
        } else {
            mMinimumWordSpacingBaseCharacter = minimumWordSpacingBaseCharacter;
            mMinimumWordSpacingBasePercentage = minimumWordSpacingBasePercentage;
            return this;
        }
    }

    /**
     * Sets whether the last line should have forced full justification.
     *
     * @param forceLastLineJustification A {@code boolean} indicating whether the last line should have forced full justification.
     */
    public JustifiedTextViewBuilder setForceLastLineJustification(boolean forceLastLineJustification) {
        mForceLastLineJustification = forceLastLineJustification;
        return this;
    }

    /**
     * Builds the JustifiedTextView based on the inputs to the JustifiedTextViewBuilder.
     *
     * @return The new JustifiedTextView.
     */
    public JustifiedTextView build() {
        validate();

        if (mTextResId != 0) { mText = mContext.getResources().getText(mTextResId).toString(); }
        JustifiedTextView justifiedTextView = new JustifiedTextView(mContext, mText);

        if (mTextColorResId != 0) { mTextColor = ContextCompat.getColor(mContext, mTextColorResId); }
        justifiedTextView.setTextColor(mTextColor);

        if (mTextSizeDimen != null) {
            mTextSize = DimensionConverter.stringToDimension(mTextSizeDimen, mContext.getResources().getDisplayMetrics());
        } else if (mTextSize == 0 && mTextSizeDimen == null) {
            mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, mContext.getResources().getDisplayMetrics());
        }
        justifiedTextView.setTextSize(mTextSize);

        if (mLineSpacingExtraDimen != null) {
            mLineSpacingExtra = DimensionConverter.stringToDimension(mLineSpacingExtraDimen, mContext.getResources().getDisplayMetrics());
        }
        justifiedTextView.setLineSpacingExtra(mLineSpacingExtra);

        justifiedTextView.setLineSpacingMultiplier(mLineSpacingMultiplier);

        if (mMinimumWordSpacingBaseCharacter != null) {
            justifiedTextView.setMinimumWordSpacing(mMinimumWordSpacingBaseCharacter, mMinimumWordSpacingBasePercentage);
        } else {
            justifiedTextView.setMinimumWordSpacing(mMinimumWordSpacing);
        }

        justifiedTextView.setForceLastLineJustification(mForceLastLineJustification);

        return justifiedTextView;
    }

    /**
     * Performs some additional validation.
     */
    private void validate(){
        if (mText != null && mTextResId != 0){
            throw new IllegalArgumentException("setText(String) and SetText(int) cannot be used together.");
        }

        if (mTextColor != 0 && mTextColorResId != 0){
            throw new IllegalArgumentException("setTextColor() and setTextColorResId() cannot be used together.");
        }

        if (mTextSize != 0 && mTextSizeDimen != null){
            throw new IllegalArgumentException("setTextSize(float) and setTextSize(String) cannot be used together.");
        }

        if (mLineSpacingExtra != 0 && mLineSpacingExtraDimen != null){
            throw new IllegalArgumentException("setLineSpacingExtra(float) and setLineSpacingExtra(String) cannot be used together.");
        }

        if (mMinimumWordSpacing > 0 && mMinimumWordSpacingBaseCharacter != null) {
            throw new IllegalArgumentException("setMinimumWordSpacing(float) and setMinimumWordSpacing(String) cannot be used together.");
        }
    }

}
