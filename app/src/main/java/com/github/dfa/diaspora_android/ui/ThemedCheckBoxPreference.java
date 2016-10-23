package com.github.dfa.diaspora_android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.util.AppLog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Customizable CheckboxPreference-like widget
 * Created by vanitas on 23.10.16.
 */

public class ThemedCheckBoxPreference extends RelativeLayout {

    @BindView(R.id.preference__themed_checkbox__title)
    protected TextView title;
    @BindView(R.id.preference__themed_checkbox__summary)
    protected TextView summary;
    @BindView(R.id.preference__themed_checkbox__checkbox)
    protected CheckBox checkBox;

    protected String prefKey;
    protected boolean defaultValue;

    public ThemedCheckBoxPreference(Context context) {
        super(context);
        init(context, null);
    }

    public ThemedCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ThemedCheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.preference__themed_checkbox, this);
        ButterKnife.bind(this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.performClick();
            }
        });

        // Assign custom attributes
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ThemedCheckBoxPreference,
                    0, 0);

            String titleText = "";
            String summaryText = "";

            try {
                titleText = a.getString(R.styleable.ThemedCheckBoxPreference_titleText);
                summaryText = a.getString(R.styleable.ThemedCheckBoxPreference_summaryText);
                prefKey = a.getString(R.styleable.ThemedCheckBoxPreference_prefKey);
                defaultValue = a.getBoolean(R.styleable.ThemedCheckBoxPreference_defaultValue, false);
            } catch (Exception e) {
                AppLog.e(this, "There was an error loading attributes.");
            } finally {
                a.recycle();
            }

            final AppSettings appSettings = new AppSettings(context);

            setTitleText(titleText);
            setSummaryText(summaryText);
            setChecked(appSettings.getThemedCheckboxPreferenceBoolean(this));
            setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    appSettings.setThemedCheckboxPreferenceBoolean(ThemedCheckBoxPreference.this, b);
                }
            });
        }
    }

    public void setTitleText(String text) {
        title.setText(text);
    }

    public void setSummaryText(String text) {
        summary.setText(text);
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }


    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        checkBox.setOnCheckedChangeListener(listener);
    }

    public String getPrefKey() {
        return this.prefKey;
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }
}
