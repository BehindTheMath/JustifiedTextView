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

package io.behindthemath.justifiedtextview.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import io.behindthemath.justifiedtextview.JustifiedTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        JustifiedTextView justifiedTextView = new JustifiedTextView(this, getResources().getString(R.string.sample_text));
        justifiedTextView.setTextColor(0x8A0000FF);
        justifiedTextView.setTextSizeDimen("12sp");
        justifiedTextView.setForceLastLineJustification(true);
        justifiedTextView.setLineSpacingExtraDimen("4dp");
        justifiedTextView.setLineSpacingMultiplier(1.5f);
        justifiedTextView.setMinimumWordSpacing("n");
        justifiedTextView.setBackgroundColor(Color.LTGRAY);

        ((LinearLayout) findViewById(R.id.linearLayout)).addView(justifiedTextView);
    }
}
