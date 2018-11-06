package com.xinzy.component.java.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.xinzy.component.R;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_PARENT;

public class NoticeBoard extends ViewFlipper implements View.OnClickListener {
    private static final String TAG = "NoticeBoard";

    private Context mContext;

    private List<String> mTexts;

    private int mTextSize;
    private int mTextColor;

    public NoticeBoard(Context context) {
        this(context, null);
    }

    public NoticeBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NoticeBoard);
        mTextSize = ta.getInt(R.styleable.NoticeBoard_textSize, 14);
        mTextColor = ta.getColor(R.styleable.NoticeBoard_textColor, 0xFF000000);
        ta.recycle();

        Animation in = new TranslateAnimation(RELATIVE_TO_PARENT, 0, RELATIVE_TO_PARENT, 0,
                RELATIVE_TO_PARENT, 1, RELATIVE_TO_PARENT, 0);
        in.setDuration(500);
        setInAnimation(in);

        Animation out = new TranslateAnimation(RELATIVE_TO_PARENT, 0, RELATIVE_TO_PARENT, 0,
                RELATIVE_TO_PARENT, 0, RELATIVE_TO_PARENT, -1);
        out.setDuration(500);
        setOutAnimation(out);
        setAutoStart(false);

        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: " + getDisplayedChild());
    }

    public void setTexts(List<String> texts) {
        if (texts == null || texts.size() == 0) {
            mTexts = null;
            stopFlipping();
            removeAllViews();
        } else {
            mTexts = texts;
            stopFlipping();
            removeAllViews();

            for (String text : texts) {
                MarqueeTextView textView = new MarqueeTextView(mContext);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
                textView.setTextColor(mTextColor);
                textView.setText(text);
                addView(textView);
            }
            startFlipping();
        }
    }

    private class MarqueeTextView extends TextView {
        public MarqueeTextView(Context context) {
            super(context);

            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            setTextColor(0xFFFF8447);
            setSingleLine();
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
            setMarqueeRepeatLimit(-1);
        }

        @Override
        public boolean isFocused() {
            return true;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NoticeBoard view, int position);
    }
}
