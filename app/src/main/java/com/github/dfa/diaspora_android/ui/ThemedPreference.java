package com.github.dfa.diaspora_android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.util.AppLog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Customizable clickable Preference class
 * Created by vanitas on 23.10.16.
 */

public class ThemedPreference extends RelativeLayout implements ThemedPreferenceObject<Void> {

    @BindView(R.id.preference__themed_preference_title)
    protected TextView title;
    @BindView(R.id.preference__themed_preference_summary)
    protected TextView summary;
    @BindView(R.id.preference__themed_preference_image)
    protected ImageView image;

    protected String prefKey;

    public ThemedPreference(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ThemedPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ThemedPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    protected void init(Context context, final AttributeSet attrs, int defStyle) {
        View.inflate(context, R.layout.preference__themed_preference, this);
        ButterKnife.bind(this);
        if (attrs != null) {
            String titleText = "";
            String summaryText = "";
            Drawable imageDrawable = null;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThemedPreference, defStyle, 0);
            try {
                titleText = a.getString(R.styleable.ThemedPreference_titleText);
                summaryText = a.getString(R.styleable.ThemedPreference_summaryText);
                prefKey = a.getString(R.styleable.ThemedPreference_prefKey);
                imageDrawable = a.getDrawable(R.styleable.ThemedPreference_image);
            } catch (Exception e) {
                AppLog.e(this, "There was an error loading attributes.");
            } finally {
                a.recycle();
            }
            setTitleText(titleText);
            setSummaryText(summaryText);
            setImageDrawable(imageDrawable);
        }
    }

    @Override
    public String getPrefKey() {
        return prefKey;
    }

    @Override
    public void setTitleText(String titleText) {
        title.setText(titleText);
        if(titleText == null || titleText.equals("")) {
            title.setVisibility(GONE);
        } else {
            title.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setSummaryText(String summaryText) {
        summary.setText(summaryText);
        if(summaryText == null || summaryText.equals("")) {
            summary.setVisibility(GONE);
        } else {
            summary.setVisibility(VISIBLE);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        if(drawable != null) {
            image.setImageDrawable(drawable);
            image.setVisibility(VISIBLE);
        } else {
            image.setImageDrawable(null);
            image.setVisibility(GONE);
        }
    }

    @Override
    public Void getDefaultValue() {
        return null;
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public void setValue(Void value) {
        /* Nope */
    }
}
