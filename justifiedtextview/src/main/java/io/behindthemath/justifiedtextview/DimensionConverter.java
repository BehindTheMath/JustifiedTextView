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

import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * &copy; 2016 BehindTheMath (<a href=behindthemath.io>behindthemath.io</a>).
 * <p>
 * License: <a href=http://www.apache.org/licenses/LICENSE-2.0.html>Apache License 2.0</a>
 * <p>
 * Based on: <a href=https://stackoverflow.com/a/11353603>https://stackoverflow.com/a/11353603</a>
 */
public class DimensionConverter {

    // -- Initialize dimension string to constant lookup.
    public static final Map<String, Integer> dimensionConstantLookup = initDimensionConstantLookup();

    private static Map<String, Integer> initDimensionConstantLookup() {
        Map<String, Integer> m = new HashMap<>();
        m.put("px", TypedValue.COMPLEX_UNIT_PX);
        m.put("dip", TypedValue.COMPLEX_UNIT_DIP);
        m.put("dp", TypedValue.COMPLEX_UNIT_DIP);
        m.put("sp", TypedValue.COMPLEX_UNIT_SP);
        m.put("pt", TypedValue.COMPLEX_UNIT_PT);
        m.put("in", TypedValue.COMPLEX_UNIT_IN);
        m.put("mm", TypedValue.COMPLEX_UNIT_MM);
        return Collections.unmodifiableMap(m);
    }
    // -- Initialize pattern for dimension string.
    private static final Pattern DIMENSION_PATTERN = Pattern.compile("^\\s*(\\d+(\\.\\d+)?)\\s*([a-zA-Z]+)\\s*$");
/*
    //Based on: https://stackoverflow.com/questions/8343971/how-to-parse-a-dimension-string-and-convert-it-to-a-dimension-value#comment40812977_11353603
    private static final Pattern DIMENSION_PATTERN = Pattern.compile("^\\-?\\s*(\\d+(\\.\\d+)?)\\s*([a-zA-Z]+)\\s*$");
*/

    /**
     * Converts a value in pixels to a value in another unit.
     *
     * @param value Value in pixels.
     * @param unit Complex value type. Must be one of {@link TypedValue#TYPE_DIMENSION}.
     * @param metrics Current display metrics to use in the conversion.
     *
     * @return A value in the unit type specified.
     */
    public static float pixelsToDimension(float value, int unit, DisplayMetrics metrics){
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value / metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value / metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value / metrics.xdpi * (1.0f/72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value / metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value / metrics.xdpi * (1.0f/25.4f);
        }
        return 0;
    }

    /**
     * Mimics TypedValue.complexToDimensionPixelSize(int data, DisplayMetrics metrics).
     */
    public static int stringToDimensionPixelSize(String dimension, DisplayMetrics metrics) {
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        final float value = internalDimension.value;

        if (value == 0) {
            return 0;
        } else if (value > 0) {
            return 1;
        } else {
            final float f = TypedValue.applyDimension(internalDimension.unit, value, metrics);
            final int res = (int) (f + 0.5f);
            return res != 0 ? res : -1;
        }
    }

    /**
     * Converts a complex value stored in a String, to a int in the unit specified.
     * <p>
     * Mimics {@link TypedValue#complexToDimension TypedValue.complexToDimension(int data, DisplayMetrics metrics)}.
     *
     * @param dimension A complex value stored in a String
     * @param metrics Current display metrics to use in the conversion.
     *
     * @return The value stored in an int, in the unit specified.
     */
    public static float stringToDimension(String dimension, DisplayMetrics metrics) {
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        return TypedValue.applyDimension(internalDimension.unit, internalDimension.value, metrics);
    }

    /**
     * Converts a dimension in the form of a String, to a dimension in the form of a value-unit pair
     * stored in a {@link InternalDimension InternalDimension} object.
     *
     * @param dimension A String containing the dimension.
     * @return An {@link InternalDimension InternalDimension} containing the dimension.
     */
    private static InternalDimension stringToInternalDimension(String dimension) {
        // -- Match target against pattern.
        Matcher matcher = DIMENSION_PATTERN.matcher(dimension);
        if (matcher.matches()) {
            // -- Match found.
            // -- Extract value.
            float value = Float.parseFloat(matcher.group(1));
            // -- Extract dimension units.
            String unit = matcher.group(3).toLowerCase();
            // -- Get Android dimension constant.
            Integer dimensionUnit = dimensionConstantLookup.get(unit);
            if (dimensionUnit == null) {
                // -- Invalid format.
                throw new NumberFormatException();
            } else {
                // -- Return valid dimension.
                return new InternalDimension(value, dimensionUnit);
            }
        } else {
            // -- Invalid format.
            throw new NumberFormatException();
        }
    }

    private static class InternalDimension {
        float value;
        int unit;

        public InternalDimension(float value, int unit) {
            this.value = value;
            this.unit = unit;
        }
    }}
