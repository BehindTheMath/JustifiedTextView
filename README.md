[ ![Download](https://api.bintray.com/packages/behindthemath/maven/justifiedtextview/images/download.svg) ](https://bintray.com/behindthemath/maven/justifiedtextview/_latestVersion)

# JustifiedTextView
JustifiedTextView is a custom view that displays text with full justification. This is as opposed to the standard `TextView`, which only allows right- or left-justification (depending on the *gravity* attribute).

<img src="http://i.imgur.com/Jatiyjn.png" width="60%" />

## Usage
XML usage:
```
<io.behindthemath.justifiedtextview.JustifiedTextView
    android:id="@+id/jtv"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="8dp"
    android:text="@string/sample_text"
    android:textSize="14sp"
    android:lineSpacingMultiplier="1.0"
    android:lineSpacingExtra="0sp"
    custom:minimumWordSpacing="0.0"
    custom:minimumWordSpacingBaseCharacter="n"
    custom:forceLastLineJustification="false"
    custom:minimumWordSpacingBasePercentage="0.8"/>
```

Code usage
```
String sample_text = getResources().getString(R.string.sample_text);

JustifiedTextView justifiedTextView = new JustifiedTextView(this, sample_text);
justifiedTextView.setTextColor(0x8A0000FF);
justifiedTextView.setTextSizeDimen("12sp");
justifiedTextView.setForceLastLineJustification(true);
justifiedTextView.setLineSpacingExtraDimen("4dp");
justifiedTextView.setLineSpacingMultiplier(1.5f);
justifiedTextView.setMinimumWordSpacing("n");
justifiedTextView.setBackgroundColor(Color.LTGRAY);

((LinearLayout) findViewById(R.id.linearLayout)).addView(justifiedTextView);
```

## Installation
Using Gradle:
1. Make sure the `repositories` block in your project's `build.gradle` includes `jcenter()` (This is now the default in Android Studio).
2. Add the following line to the `dependencies` block in your app module's `build.gradle`:
```
compile 'io.behindthemath.justifiedtextview:justifiedtextview:x.y.z'
```
x.y.z is the latest version available - see the top of README.

## License

    Copyright 2016 Behind The Math

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.