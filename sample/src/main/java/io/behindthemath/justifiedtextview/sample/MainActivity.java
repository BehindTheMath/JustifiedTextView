package io.behindthemath.justifiedtextview.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.behindthemath.justifiedtextview.JustifiedTextView;

/**
 * &copy; 2016 BehindTheMath (<a href=behindthemath.io>behindthemath.io</a>).
 * <p>
 * License: <a href=http://www.apache.org/licenses/LICENSE-2.0.html>Apache License 2.0</a>
 */
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

        TextView textView = new TextView(this);
        textView.setText("JustifiedTextView from Builder:");
        ((LinearLayout) findViewById(R.id.linearLayout)).addView(textView);

        JustifiedTextView justifiedTextView2 = JustifiedTextView.with(this).setText(R.string.sample_text)
                .setTextColor(0x8A0000FF).setTextSize("12sp").setForceLastLineJustification(true).setLineSpacingExtra("4dp")
                .setLineSpacingMultiplier(1.5f).setMinimumWordSpacing("n").build();
        justifiedTextView2.setBackgroundColor(Color.LTGRAY);
        ((LinearLayout) findViewById(R.id.linearLayout)).addView(justifiedTextView2);
    }
}
