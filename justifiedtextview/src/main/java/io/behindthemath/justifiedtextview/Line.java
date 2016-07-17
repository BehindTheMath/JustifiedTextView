package io.behindthemath.justifiedtextview;

import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.IllegalFormatException;

/**
 * An object that represents 1 line after the word spacing and words per line have been calculated.
 * <p>
 * &copy; 2016 BehindTheMath (<a href=behindthemath.io>behindthemath.io</a>).
 * <p>
 * License: <a href=http://www.apache.org/licenses/LICENSE-2.0.html>Apache License 2.0</a>
 */
public class Line {
    private ArrayList<String> words = new ArrayList<>();
    private float spacing;

    public Line() {}

    /**
     * Copy constructor.
     *
     * @param words An {@link ArrayList} of {@link String}s, each one representing 1 word.
     * @param spacing The word spacing for this line, in pixels.
     *
     * @throws IllegalArgumentException If {@code words} is {@code null} or {@code spacing} is not more than 0.
     */
    public Line(@NonNull ArrayList<String> words,@FloatRange(from = 0, fromInclusive = false)  float spacing) {
        if (words != null && spacing > 0) {
            this.words = words;
            this.spacing = spacing;
        } else if (words == null) {
            throw new IllegalArgumentException("words cannot be null.");
        } else if (spacing <= 0) {
            throw new IllegalArgumentException("Spacing must be more than 0.");
        }
    }

    /**
     * Sets word the spacing of the current line.
     *
     * @param spacing The word spacing for this line, in pixels.
     */
    public void setSpacing(@FloatRange(from = 0, fromInclusive = false) float spacing) {
        if (spacing > 0) {
            this.spacing = spacing;
        } else {
            throw new IllegalArgumentException("Spacing must be more than 0.");
        }
    }

    /**
     * Returns the word spacing for this line, in pixels.
     * @return The word spacing for this line, in pixels.
     */
    public float getSpacing() {
        return spacing;
    }

    /**
     * Appends a word to the end of the line.
     *
     * @param word A word to append to the end of the line.
     */
    public void addWord(String word) {
        if (word.contains(" ")){
            throw new IllegalArgumentException("word must be only 1 word.");
        } else {
            words.add(word);
        }
    }

    /**
     * Resets the line to an ArrayList of Strings representing the words on the line.
     *
     * @param words An ArrayList of Strings representing the words on the line.
     */
    public void setWords(ArrayList<String> words){
        this.words = words;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    /**
     * Returns the full text of the line, i.e. all the words concatenated with spaces.
     *
     * @return The full text of the line.
     */
    public String getFullLineText(){
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words){
            stringBuilder.append(word).append(" ");
        }
        return stringBuilder.toString().trim();
    }

    /**
     * Returns the number of words on the line.
     *
     * @return The number of words on the line.
     */
    public int getWordCount() { return words.size(); }

    /**
     * Returns the total width of all the words in the line, in pixels.
     *
     * @param textPaint A {@link TextPaint} object used to calculate the width of the words.
     *
     * @return The total width of all the words in the line, in pixels.
     *
     * @throws IllegalArgumentException If {@code textPaint} is {@code null}.
     */
    public float getTotalWordWidth(@NonNull TextPaint textPaint){
        if (textPaint != null) {
            float totalWordWidth = 0;
            for (String word : words) {
                totalWordWidth += textPaint.measureText(word);
            }
            return totalWordWidth;
        } else {
            throw new IllegalArgumentException("textPaint cannot be null.");
        }
    }

    /**
     * Returns the total width of all the spaces in the line, in pixels.
     *
     * @return The total width of all the spaces in the line, in pixels.
     */
    public float getTotalSpaceWidth(){
        return (words.size() - 1 ) * spacing;
    }

    /**
     * Returns the total width of the line, both words and spaces, in pixels.
     *
     * @param textPaint A {@link TextPaint} object used to calculate the width of the words.
     *
     * @return The total width of the line, both words and spaces, in pixels.
     *
     * @throws IllegalArgumentException If {@code textPaint} is {@code null}.
     */
    public float getTotalLineWidth(@NonNull TextPaint textPaint){
        if (textPaint != null) {
            return getTotalWordWidth(textPaint) + getTotalSpaceWidth();
        } else {
            throw new IllegalArgumentException("textPaint cannot be null.");
        }
    }

    /**
     * Resets the line.
     */
    public void clear(){
        words.clear();
        spacing = 0f;
    }

    /**
     * Returns a duplicate of the line.
     *
     * @return A duplicate of the line.
     */
    public Line duplicate(){
        return new Line(new ArrayList<>(this.words), this.spacing);
    }
}
