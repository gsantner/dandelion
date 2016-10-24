package com.github.dfa.diaspora_android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Customizable CheckboxPreference-like widget
 * Created by vanitas on 23.10.16.
 */

public class ThemedCheckBoxPreference extends RelativeLayout implements ThemedPreferenceObject<Boolean> {

    @BindView(R.id.preference__themed_checkbox__title)
    protected TextView title;
    @BindView(R.id.preference__themed_checkbox__summary)
    protected TextView summary;
    @BindView(R.id.preference__themed_checkbox__checkbox)
    protected CheckBox checkBox;

    protected String prefKey;
    protected boolean defaultValue;
    protected AppSettings appSettings;
    protected CompoundButton.OnCheckedChangeListener externalOnCheckedChangedListener;

    public ThemedCheckBoxPreference(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ThemedCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ThemedCheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    protected void init(Context context, AttributeSet attrs, int defStyle) {
        appSettings = new AppSettings(context.getApplicationContext());
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
            String titleText = "";
            String summaryText = "";

            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ThemedCheckBoxPreference, defStyle, 0);
            try {
                titleText = a.getString(R.styleable.ThemedCheckBoxPreference_titleText);
                summaryText = a.getString(R.styleable.ThemedCheckBoxPreference_summaryText);
                prefKey = a.getString(R.styleable.ThemedCheckBoxPreference_prefKey);
                defaultValue = a.getBoolean(R.styleable.ThemedCheckBoxPreference_defaultBoolean, false);
            } catch (Exception e) {
                AppLog.e(this, "There was an error loading attributes.");
            } finally {
                a.recycle();
            }

            setTitleText(titleText);
            if(titleText == null || titleText.equals("")) {
                title.setVisibility(GONE);
            }
            setSummaryText(summaryText);
            if(summaryText == null || summaryText.equals("")) {
                summary.setVisibility(GONE);
            }
            setChecked(appSettings.getThemedCheckboxPreferenceValue(this));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    appSettings.setThemedCheckboxPreferenceValue(ThemedCheckBoxPreference.this, b);
                    if(externalOnCheckedChangedListener != null) {
                        externalOnCheckedChangedListener.onCheckedChanged(compoundButton, b);
                    }
                }
            });
            applyColor();
        }
    }

    public void applyColor() {
        ThemeHelper.getInstance(appSettings);
        ThemeHelper.updateCheckBoxColor(checkBox);
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

    public void setOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener listener) {
        externalOnCheckedChangedListener = listener;
    }

    public String getPrefKey() {
        return this.prefKey;
    }

    public Boolean getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public Boolean getValue() {
        return appSettings.getThemedCheckboxPreferenceValue(this);
    }

    @Override
    public void setValue(Boolean value) {
        appSettings.setThemedCheckboxPreferenceValue(this, value);
    }
}
