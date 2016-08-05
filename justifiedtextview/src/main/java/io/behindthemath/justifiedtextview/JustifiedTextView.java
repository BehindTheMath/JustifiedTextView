/*
 * Copyright (c) 2016 Behind The Math
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.behindthemath.justifiedtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StringRes;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * This class is a basic View that displays text, with full justification.
 * This is as opposed to {@link android.widget.TextView}, which supports only left- or right-justification using the <em>gravity</em> attribute.
 */
public class JustifiedTextView extends View {
    static final float DEFAULT_WORD_SPACING_BASE_PERCENTAGE = 0.8f;
    /**
     * The logging tag used by this class with {@link android.util.Log}.
     */
    protected final static String LOG_TAG = "JustifiedTextView";

    /**
     * Text to be displayed.
     */
    private String mText = "";

    /**
     * Text size in pixels. Default is 14sp.
     */
    private float mTextSize = 0;

    /**
     * A color value for the text. Default value is BLACK (0xFFFFFFFF).
     */
    @ColorInt
    private int mTextColor = Color.BLACK;

    /**
     * Number to multiply the text size by to get the line height.
     * Must equal at least 1.0. Default value is 1.15f.
     */
    @FloatRange(from = 1f)
    private float mLineSpacingMultiplier = 1.15f;

    /**
     * Extra vertical space to add between lines.
     * This is added after the line spacing is calculated based on mLineSpacingMultiplier.
     * Default value is 0.
     */
    private float mLineSpacingExtra = 0;

    /**
     * Line height in pixels, including spacing above.
     */
    private float mLineHeight = 0;

    /**
     * Percentage to multiply the minimum word spacing character by to get the minimum word spacing.
     * Must equal 0 - 1.0f. Default value is 0.8f.
     */
    @FloatRange(from = 0, to = 1f)
    private float mMinimumWordSpacingBasePercentage = 0;

    /**
     * Character to base the minimum word spacing on.
     * Default value equals the width of 1 space.
     */
    @Nullable
    private String mMinimumWordSpacingBaseCharacter = null;

    /**
     * Minimum word spacing, in pixels.
     */
    @FloatRange(from = 0)
    private float mMinimumWordSpacing = 0;

    /**
     * Whether to force full justification on the last line of a paragraph.
     * Default value is {@code false}.
     */
    private boolean mForceLastLineJustification = false;

    /**
     * {@link android.text.TextPaint} object that holds the formatting options for the text.
     */
    private TextPaint mTextPaint = new TextPaint(ANTI_ALIAS_FLAG);

    /**
     * ArrayList of lines of text after justification has been calculated.
     */
    private ArrayList<Line> mLinesList = new ArrayList<>();

    /**
     * Temporary {@link Line} object used to calculate word spacing.
     */
    private Line lineBuffer = new Line();

    /**
     * The left padding, in pixels.
     */
    private int mPaddingLeft;

    /**
     * The top padding, in pixels
     */
    private int mPaddingTop;

    /**
     * The right padding, in pixels
     */
    private int mPaddingRight;

    /**
     * Denotes whether the text is RTL.
     */
    private boolean mRTL = false;

    /**
     * Bare minimum constructor to use when creating the view from code.
     * After calling the constructor, use the setters to set the attributes.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     */
    public JustifiedTextView(Context context) {
        this(context, "");
    }

    /**
     * Constructor to use when creating the view from code, with text from a string resource.
     * After calling the constructor, the setters can be used to set the rest of the attributes.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param resid The resource ID of a string to set as the text to be displayed by the view.
     */
    public JustifiedTextView(Context context, @StringRes int resid) {
        this(context, context.getResources().getText(resid).toString());
    }

    /**
     * Constructor to use when creating the view from code, with text.
     * After calling the constructor, the setters can be used to set the rest of the attributes.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param text The text to display.
     */
    public JustifiedTextView(Context context, String text) {
        super(context);

        mText = text;
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.getResources().getDisplayMetrics());
        /*
         * Calculate the line height, based on the text size and line spacing.
         */
        mLineHeight = mTextSize * mLineSpacingMultiplier;

        /*
         * Set the attributes of the TextPaint.
         */
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        /*
         * Calculate the default word spacing.
         * Default value is 80% of the width of 1 space.
         */
        //mDefaultWordSpacing = mTextPaint.measureText(" ");
        mMinimumWordSpacing = mTextPaint.measureText(" ") * 0.8f;

    }

    /**
     * Constructor called when inflating from XML.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public JustifiedTextView(Context context, AttributeSet attrs){
        super(context, attrs);

        /*
         * Get the default text color, which equals textColorSecondary from the current theme.
         */
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, new int[] {android.R.attr.textColorSecondary});
        @ColorInt int defaultTextColor = typedArray.getColor(0, Color.BLACK);
        typedArray.recycle();

        /*
         * Get the XML attributes, if there are any.
         */
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JustifiedTextView, 0, 0);
        mText = typedArray.getString(R.styleable.JustifiedTextView_android_text);
        if (mText == null) { mText = ""; }
        /*
         * Default text size is 14sp.
         */
        float defaultTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, context.getResources().getDisplayMetrics());
        mTextSize = typedArray.getDimension(R.styleable.JustifiedTextView_android_textSize, defaultTextSize);
        mTextColor = typedArray.getColor(R.styleable.JustifiedTextView_android_textColor, defaultTextColor);

        mLineSpacingMultiplier = typedArray.getFloat(R.styleable.JustifiedTextView_android_lineSpacingMultiplier, 1.15f);
        mLineSpacingExtra = typedArray.getDimension(R.styleable.JustifiedTextView_android_lineSpacingExtra, 0);
        mForceLastLineJustification = typedArray.getBoolean(R.styleable.JustifiedTextView_forceLastLineJustification, false);

        /*
         * Calculate the minimum word spacing.
         *
         * Order of priority is:
         *     The XML minimumWordSpacing attribute.
         *     The XML minimumWordSpacingBase attribute multiplied by the XML minimumWordSpacingBasePercentage attribute.
         *     Width of 1 space.
         */
        mMinimumWordSpacing = typedArray.getFloat(R.styleable.JustifiedTextView_minimumWordSpacing, 0);
        if (mMinimumWordSpacing == 0) {
            String minimumWordSpacingBaseCharacter = typedArray.getString(R.styleable.JustifiedTextView_minimumWordSpacingBaseCharacter);
            float minimumWordSpacingBasePercentage = typedArray.getFloat(R.styleable.JustifiedTextView_minimumWordSpacingBasePercentage, 0.8f);
            if (minimumWordSpacingBaseCharacter != null && minimumWordSpacingBasePercentage != 0) {
                if (minimumWordSpacingBaseCharacter.length() == 1) {
                    mMinimumWordSpacing = mTextPaint.measureText(minimumWordSpacingBaseCharacter) * minimumWordSpacingBasePercentage;
                } else {
                    throw new IllegalArgumentException("minimumWordSpacingBaseCharacter must be only 1 character.");
                }
            } else if ((minimumWordSpacingBaseCharacter == null) ^ (minimumWordSpacingBasePercentage != 0)) {
                throw new IllegalArgumentException("Both minimumWordSpacingBaseCharacter and minimumWordSpacingBasePercentage must to be set.");
            } else {
                mMinimumWordSpacing = mTextPaint.measureText(" ") * 0.8f;
            }
        }

        mRTL = typedArray.getBoolean(R.styleable.JustifiedTextView_RTL, false);

        typedArray.recycle();

        /*
         * mLineSpacingMultiplier cannot be less than 1.0. If it is, set it to 1.0.
         */
        if (mLineSpacingMultiplier < 1f) { mLineSpacingMultiplier = 1f; }
        /*
         * Calculate the line height, based on the text size and line spacing.
         */
        mLineHeight = mTextSize * mLineSpacingMultiplier + mLineSpacingExtra;

        /*
         * Set the attributes of the TextPaint.
         */
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    /**
     * Measures the view and its content to determine the measured width and the measured height, by calculating each line.
     *
     * @param widthMeasureSpec {@inheritDoc}
     * @param heightMeasureSpec {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float wordWidth, totalWordWidth, proposedLineWidth;
        float proposedWordSpacing = 0, currentWordSpacing = 0;
        int proposedNumSpacesNeeded;
        float height;

        /*
         * Get the padding values from the parent View.
         */
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        /*
         * Calculate the width of the view. The height will be calculated later.
         */
        float width = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 1);

        mLinesList.clear();
        /*
         * Split the text into paragraphs, and process each one.
         */
        String[] paragraphs = mText.split("\n");
        for (String paragraph : paragraphs) {
            totalWordWidth = 0;
            lineBuffer.clear();
            /*
             * Split each paragraph into words.
             */
            String[] words = paragraph.split(" ");
            for (String word : words) {
                /*
                 * Calculate the width of the word.
                 */
                wordWidth = mTextPaint.measureText(word);
                /*
                 * Get the number of spaces that will be required if this word is included on the current line.
                 */
                proposedNumSpacesNeeded = lineBuffer.getWordCount();
                /*
                 * Calculate the width of the line if this word is included.
                 */
                proposedLineWidth = (float) mPaddingLeft + (float) mPaddingRight + totalWordWidth + wordWidth + (mMinimumWordSpacing * (float) proposedNumSpacesNeeded);
                /*
                 * Calculate the word spacing if this word is included on the current line.
                 */
                proposedWordSpacing = (width - (float) mPaddingLeft - (float) mPaddingRight - totalWordWidth - wordWidth) / (float) proposedNumSpacesNeeded;
                /*
                 * If the new word will fit (i.e. the line is not too wide, and the word spacing is not too narrow):
                 */
                if ((Float.compare(proposedLineWidth, width) < 0) && (Float.compare(proposedWordSpacing, mMinimumWordSpacing) >= 0)) {
                    /*
                     * Add the word to the current line, and recalculate the new values for the line.
                     */
                    lineBuffer.addWord(word);
                    totalWordWidth += wordWidth;
                    currentWordSpacing = proposedWordSpacing;
                /*
                 * If the new word fits exactly (i.e. the line is exactly the same width as the view, and the word spacing is not too narrow):
                 */
                } else if ((Float.compare(proposedLineWidth, width) == 0) && (Float.compare(proposedWordSpacing, mMinimumWordSpacing) >= 0)) {
                    /*
                     * Add the word to the current line, and recalculate the new values for the line.
                     */
                    lineBuffer.addWord(word);
                    //totalWordWidth += wordWidth;
                    currentWordSpacing = proposedWordSpacing;
                    /*
                     * Store the final word spacing for this line.
                     */
                    lineBuffer.setSpacing(currentWordSpacing);
                    /*
                     * Add this line to the list of already calculated lines.
                     */
                    mLinesList.add(lineBuffer.duplicate());
                    /*
                     * Set up for the next line.
                     */
                    totalWordWidth = 0;
                    lineBuffer.clear();
                /*
                 * If the new word won't fit (i.e. the line is too wide, or the word spacing is too narrow):
                 */
                } else if ((Float.compare(proposedLineWidth, width) > 0) || (Float.compare(proposedWordSpacing, mMinimumWordSpacing) < 0)) {
                    /*
                     * Store the final word spacing for this line.
                     */
                    lineBuffer.setSpacing(currentWordSpacing);
                    /*
                     * Add this line to the list of already calculated lines.
                     */
                    mLinesList.add(lineBuffer.duplicate());
                    /*
                     * Set up the new line.
                     */
                    lineBuffer.clear();
                    /*
                     * Add the word to the new line.
                     */
                    lineBuffer.addWord(word);
                    totalWordWidth = wordWidth;
                }
            }
            /*
             * If we finished processing all the words in a paragraph, and the last line wasn't added to the list yet:
             */
            if (lineBuffer.getWordCount() > 0) {
                /*
                 * Calculate spacing for the last line.
                 */
                lineBuffer.setSpacing(mForceLastLineJustification ? proposedWordSpacing : mMinimumWordSpacing);
                /*
                 * Add it to the list.
                 */
                mLinesList.add(lineBuffer.duplicate());
            }
        }

        /*
         * Once all the lines are calculated, calculate the required size of the view.
         */
        height = (mLinesList.size() * mLineHeight + mPaddingTop + paddingBottom);
        /*
         * If the required height is less than the minimum height, expand it.
         */
        if (Float.compare(height, getSuggestedMinimumHeight()) < 0){ height = getSuggestedMinimumHeight(); }
        /*
         * If the required height is bigger than the maximum height, part of the view will be cut off.
         */
        int intHeight = resolveSizeAndState((int) height, heightMeasureSpec, 1);
        /*
         * Set the height and width.
         */
        setMeasuredDimension((int) width, intHeight);
    }

    /**
     * Performs the actual drawing of the text.
     *
     * @param canvas A {@link android.graphics.Canvas} object to draw on.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*
         * Line spacing is divided into 2 parts, with half above the line, and half below.
         * For the first line, calculate the spacing above.
         */
        float lineSpacingAbove = ((mLineSpacingMultiplier - 1f) * mTextSize + mLineSpacingExtra) / 2f;
        /*
         * x is the horizontal position in the canvas in pixels, starting from the left.
         */
        float x;
        /*
         * y is the vertical position in the canvas in pixels, starting from the top.
         * The y-value in drawText() is based on the baseline of the text.
         * For the first line, calculate the y-value of the baseline, by adding the top padding, the line spacing above, and the height of the text.
         */
        float y = (float) mPaddingTop + lineSpacingAbove + mTextSize;

        for (Line line : mLinesList) {
            /*
             * If this line has words on it, and is not just a paragraph break:
             */
            if (line.getWordCount() > 0) {
                /*
                 * If the text is RTL:
                 */
                if (mRTL) {
                    /*
                     * Move x to the left to account for the right padding.
                     * The left padding does not need to be explicitly accounted for, since the line width already accounts for it, and it will automatically be left blank.
                     */
                    x = getWidth() - mPaddingRight;
                    /*
                     * Loop through each word on the line.
                     */
                    for (String word : line.getWords()) {
                        /*
                         * Loop through each letter in the word.
                         */
                        for (int i = 0; i < word.length(); i++) {
                            String s = word.substring(i, i + 1);
                            /*
                             * Move over to the right, the width of the letter.
                             */
                            x -= mTextPaint.measureText(s);
                            /*
                             * Draw the letter.
                             */
                            canvas.drawText(s, x, y, mTextPaint);
                        }
                        x -= line.getSpacing();
                    }
                } else {
                    /*
                     * Move x to the right to account for the left padding.
                     * The right padding does not need to be explicitly accounted for, since the line width already accounts for it, and it will automatically be left blank.
                     */
                    x = mPaddingLeft;
                    for (String word : line.getWords()) {
                        /*
                         * Draw each word on the canvas.
                         */
                        canvas.drawText(word, x, y, mTextPaint);
                        /*
                         * Move x over to the right the width of the word and the word spacing for this line.
                         */
                        x += mTextPaint.measureText(word) + line.getSpacing();
                    }
                }
            }
            /*
             * When a line is complete, move down to the next one.
             */
            y += mLineHeight;
        }
    }

    /**
     * Returns the text that the view is displaying.
     *
     * @return The text that the view is displaying.
     */
    public String getText() {
        return mText;
    }

    /**
     * Set the text to be displayed by the view.
     *
     * @param text The text to be displayed by the view.
     *
     * @throws IllegalArgumentException If {@code text} is {@code null}.
     */
    public void setText(@NonNull String text) {
        if (text != null) {
            mText = text;
            requestLayout();
            invalidate();
        } else {
            throw new IllegalArgumentException("Text cannot be null.");
        }
    }

    /**
     * Sets the text to be displayed by the view, from a string resource.
     *
     * @param resid The resource ID of a string to set as the text to be displayed by the view.
     *
     * @throws IllegalArgumentException If {@code resid} equals 0.
     */
    public void setText(@StringRes int resid) {
        if (resid != 0) {
            setText(getContext().getResources().getText(resid).toString());
        } else {
            throw new IllegalArgumentException("resid cannot equal 0.");
        }
    }

    /**
     * Returns the text color.
     *
     * @return The text color, encoded as 0xAARRGGBB.
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Sets the text color.
     *
     * @param textColor The text color, encoded as 0xAARRGGBB.
     */
    public void setTextColor(@ColorInt int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    /**
     * Returns the text size, in pixels.
     *
     * @return The text size, in pixels.
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Returns the text size, as a dimension string, in SP.
     *
     * @return The text size, as a dimension string, in SP.
     */
    public String getTextSizeDimen() {
        return Float.toString(DimensionConverter.pixelsToDimension(mTextSize, TypedValue.COMPLEX_UNIT_SP, getContext().getResources().getDisplayMetrics())).concat("sp");
    }

    /**
     * Returns the text size as a complex value, based on {@code type}.
     *
     * @param type The {@link TypedValue} complex type to return.
     *
     * @return The text size as a complex value, based on {@code type}
     *
     * @throws IllegalArgumentException If {@code type} is not one of the {@link TypedValue} complex types, or if {@code textSize} is less than 0.
     */
    public float getTextSize(@IntRange(from = 0, to = 5) int type) {
        if (type < 0 || type > 5) {
            throw new IllegalArgumentException("type must be one of the TypedValue complex types.");
        } else {
            return TypedValue.applyDimension(type, getTextSize(), getContext().getResources().getDisplayMetrics());
        }

    }

    /**
     * Sets the text size, in pixels.
     *
     * @param textSize The text size, in pixels.
     *
     * @throws IllegalArgumentException If {@code textSize} is not more than 0.
     */
    public void setTextSize(@FloatRange(from = 0, fromInclusive = false) float textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
            mLineHeight = mTextSize * mLineSpacingMultiplier + mLineSpacingExtra;
            mTextPaint.setTextSize(mTextSize);
            invalidate();
            requestLayout();
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
    public void setTextSizeDimen(@NonNull @Size(min = 1) String textSizeDimen) {
        if (textSizeDimen != null) {
            setTextSize(DimensionConverter.stringToDimension(textSizeDimen, getContext().getResources().getDisplayMetrics()));
        } else {
            throw new IllegalArgumentException("textSizeDimen cannot be null.");
        }
    }

    /**
     * Sets the text size based on a dimension stored as a complex value type and value.
     *
     * @param type The complex value type.
     * @param textSize The dimension value.
     *
     * @throws IllegalArgumentException If {@code type} is not one of the {@link TypedValue} complex types, or if {@code textSize} is less than 0.
     */
    public void setTextSize(@IntRange(from = 0, to = 5) int type, @FloatRange(from = 0) float textSize){
        if (type < 0 || type > 5) {
            throw new IllegalArgumentException("type must be one of the TypedValue complex types.");
        } else if (textSize < 0 ) {
            throw new IllegalArgumentException("textSize must be at least 0.");
        } else {
            setTextSize(TypedValue.applyDimension(type, textSize, getContext().getResources().getDisplayMetrics()));
        }
    }

    /**
     * Returns the extra line spacing added after the base line spacing is calculated, in pixels.
     *
     * @return The extra line spacing, in pixels.
     */
    public float getLineSpacingExtra() {
        return mLineSpacingExtra;
    }

    /**
     * Sets the extra line spacing to be added after the base line spacing is calculated, in pixels.
     *
     * @param lineSpacingExtra The extra line spacing, in pixels.
     *
     * @throws IllegalArgumentException If {@code lineSpacingExtra} is less than 0.
     */
    public void setLineSpacingExtra(@FloatRange(from = 0) float lineSpacingExtra) {
        if (lineSpacingExtra >= 0) {
            mLineSpacingExtra = lineSpacingExtra;
            mLineHeight = mTextSize * mLineSpacingMultiplier + mLineSpacingExtra;
            invalidate();
            requestLayout();
        } else {
            throw new IllegalArgumentException("lineSpacingExtra cannot be less than 0.");
        }
    }

    /**
     * Returns the extra line spacing added after the base line spacing is calculated, as a dimension string.
     *
     * @return The extra line spacing, as a dimension string.
     */
    public String getLineSpacingExtraDimen() {
        return Float.toString(DimensionConverter.pixelsToDimension(mLineSpacingExtra, TypedValue.COMPLEX_UNIT_SP, getContext().getResources().getDisplayMetrics())).concat("sp");
    }

    /**
     * Sets the extra line spacing to be added after the base line spacing is calculated, as a dimension.
     *
     * @param lineSpacingExtraDimen The extra line spacing, as a dimension string.
     *
     * @throws IllegalArgumentException If {@code lineSpacingExtraDimen} is {@code null}.
     */
    public void setLineSpacingExtraDimen(@NonNull @Size(min = 1) String lineSpacingExtraDimen) {
        if (lineSpacingExtraDimen != null) {
            setLineSpacingExtra(DimensionConverter.stringToDimension(lineSpacingExtraDimen, getContext().getResources().getDisplayMetrics()));
        } else {
            throw new IllegalArgumentException("lineSpacingExtraDimen cannot be null.");
        }
    }

    /**
     * Returns the line spacing multiplier.
     *
     * @return The line spacing multiplier.
     */
    public float getLineSpacingMultiplier() {
        return mLineSpacingMultiplier;
    }

    /**
     * Sets the line spacing multiplier.
     *
     * @param lineSpacingMultiplier The line spacing multiplier. Must equal at least 1.0.
     *
     * @throws IllegalArgumentException If {@code lineSpacingMultiplier} is less than 1.0.
     */
    public void setLineSpacingMultiplier(@FloatRange(from = 1.0) float lineSpacingMultiplier) {
        if (lineSpacingMultiplier >= 1.0) {
            mLineSpacingMultiplier = lineSpacingMultiplier;
            mLineHeight = mTextSize * mLineSpacingMultiplier + mLineSpacingExtra;
            invalidate();
            requestLayout();
        } else {
            throw new IllegalArgumentException("lineSpacingMultiplier must be at least 1.0.");
        }
    }

    /**
     * Returns the minimum word spacing, in pixels.
     *
     * @return The minimum word spacing, in pixels.
     */
    public float getMinimumWordSpacing() {
        return mMinimumWordSpacing;
    }

    /**
     * Sets the minimum word spacing.
     *
     * @param minimumWordSpacing The minimum word spacing. Must be at least 0.
     *
     * @throws IllegalArgumentException If {@code minimumWordSpacing} is not more than 0.
     */
    public void setMinimumWordSpacing(@FloatRange(from = 0) float minimumWordSpacing) {
        if (minimumWordSpacing >= 0) {
            mMinimumWordSpacing = minimumWordSpacing;
            mMinimumWordSpacingBaseCharacter = null;
            mMinimumWordSpacingBasePercentage = 0;
            invalidate();
            requestLayout();
        } else {
            throw new IllegalArgumentException("minimumWordSpacing must be at least 0.");
        }
    }

    /**
     * Sets the minimum word spacing, based on a character, with a default percentage of {@code DEFAULT_WORD_SPACING_BASE_PERCENTAGE} (0.8f).
     *
     * @param minimumWordSpacingBaseCharacter A character to base the minimum word spacing on.
     */
    public void setMinimumWordSpacing(@NonNull @Size(1) String minimumWordSpacingBaseCharacter) {
        setMinimumWordSpacing(minimumWordSpacingBaseCharacter, DEFAULT_WORD_SPACING_BASE_PERCENTAGE);
    }

    /**
     * Sets the minimum word spacing, based on a character and a percentage.
     *
     * @param minimumWordSpacingBaseCharacter A character to base the minimum word spacing on.
     * @param minimumWordSpacingBasePercentage The percentage of the width of the character to use as the minimum word spacing.  Must be between 0 and 1.0. If 0, DEFAULT_WORD_SPACING_BASE_PERCENTAGE (0.8f) will be used.
     *
     * @throws IllegalArgumentException If {@code minimumWordSpacingBaseCharacter} is null, or if {@code minimumWordSpacingBasePercentage} is not between 0 and 1.0.
     */
    public void setMinimumWordSpacing(@NonNull @Size(1) String minimumWordSpacingBaseCharacter, @FloatRange(from = 0, to = 1f, fromInclusive = false) float minimumWordSpacingBasePercentage) {
        if (minimumWordSpacingBaseCharacter != null && minimumWordSpacingBasePercentage >= 0 && minimumWordSpacingBasePercentage <= 1f) {
            mMinimumWordSpacingBaseCharacter = minimumWordSpacingBaseCharacter;
            mMinimumWordSpacingBasePercentage = minimumWordSpacingBasePercentage != 0 ? minimumWordSpacingBasePercentage : DEFAULT_WORD_SPACING_BASE_PERCENTAGE;
            mMinimumWordSpacing = mTextPaint.measureText(mMinimumWordSpacingBaseCharacter) * mMinimumWordSpacingBasePercentage;
            invalidate();
            requestLayout();
        } else if (minimumWordSpacingBaseCharacter == null) {
            throw new IllegalArgumentException("minimumWordSpacingBaseCharacter cannot be null.");
        } else if (minimumWordSpacingBasePercentage < 0 || minimumWordSpacingBasePercentage > 1f) {
            throw new IllegalArgumentException("minimumWordSpacingBasePercentage must be between 0 and 1.0.");
        }
    }

    /**
     * Returns the character that the minimum word spacing is based on.
     * <p>
     * If the minimum word spacing was set with {@link #setMinimumWordSpacing setMinimumWordSpacing(float)}, this method will return {@code null}.
     *
     * @return The character the minimum word spacing is based on.
     */
    public String getMinimumWordSpacingBaseCharacter(){
        return mMinimumWordSpacingBaseCharacter;
    }

    /**
     * Returns the percentage of the character that the minimum word spacing is based on.
     * <p>
     * If the minimum word spacing was set with {@link #setMinimumWordSpacing setMinimumWordSpacing(float)}, this method will return 0.
     *
     * @return The percentage of the character that the minimum word spacing is based on.
     */
    public float getMinimumWordSpacingBasePercentage(){
        return mMinimumWordSpacingBasePercentage;
    }

    /**
     * Returns whether the last line should have forced full justification.
     *
     * @return A {@code boolean} indicating whether the last line should have forced full justification.
     */
    public boolean getForceLastLineJustification() {
        return mForceLastLineJustification;
    }

    /**
     * Sets whether the last line should have forced full justification.
     *
     * @param forceLastLineJustification A {@code boolean} indicating whether the last line should have forced full justification.
     */
    public void setForceLastLineJustification(boolean forceLastLineJustification) {
        mForceLastLineJustification = forceLastLineJustification;
        invalidate();
    }

    /**
     * Returns whether the text is RTL.
     *
     * @return Whether the text is RTL.
     */
    public boolean isRTL() {
        return mRTL;
    }

    /**
     * Sets whether the text is RTL.
     *
     * @param RTL A {@code boolean} representing whether the text is RTL.
     */
    public void setRTL(boolean RTL) {
        mRTL = RTL;
    }

    /**
     * Returns an {@link java.util.ArrayList} of {@link java.lang.String}s, each one representing the full text of the respective line displayed in the view.
     *
     * @return An {@link java.util.ArrayList} of {@link java.lang.String}s, each one representing the full text of the respective line displayed in the view.
     */
    public ArrayList<String> getLinesListAsStrings() {
        ArrayList<String> lines = new ArrayList<>();
        for (Line line : mLinesList) {
            lines.add(line.getFullLineText());
        }
        return lines;
    }

    /**
     * Returns an {@link java.util.ArrayList} of {@link Line} objects, each one representing a respective line displayed in the view.
     *
     * @return An {@link java.util.ArrayList} of {@link Line} objects, each one representing a respective line displayed in the view.
     */
    public ArrayList<Line> getLinesListAsLines() {
        return new ArrayList<>(mLinesList);
    }

    /**
     * Returns the number of lines displayed in the view.
     *
     * @return The number of lines displayed in the view.
     */
    public int getLinesCount(){
        return mLinesList.size();
    }

    /**
     * Returns the length, in characters, of the text displayed in the view.
     *
     * @return The length, in characters, of the text displayed in the view.
     */
    public int length(){
        return mText.length();
    }
}