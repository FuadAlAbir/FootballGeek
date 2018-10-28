package com.geekstudios.footballgeekblog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class KalpurushText extends TextView {

    public KalpurushText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public KalpurushText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KalpurushText(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kalpurush.ttf");
        setTypeface(tf ,1);

    }

}
