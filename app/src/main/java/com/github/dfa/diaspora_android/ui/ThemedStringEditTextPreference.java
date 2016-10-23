package com.github.dfa.diaspora_android.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.util.AppLog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Customizable EditTextPreference-like class
 * Created by vanitas on 23.10.16.
 */

public class ThemedStringEditTextPreference extends LinearLayout implements ThemedPreference<String> {
    @BindView(R.id.preference__themed_edittext_title)
    protected TextView title;
    @BindView(R.id.preference__themed_edittext_summary)
    protected TextView summary;

    protected String prefKey;
    protected String defaultValue;
    protected AppSettings appSettings;
    protected boolean showValueInSummary;
    protected OnPositiveButtonClickedListener onPositiveButtonClickedListener;

    public ThemedStringEditTextPreference(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ThemedStringEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ThemedStringEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    protected void init(final Context context, AttributeSet attrs, int defStyle) {
        appSettings = new AppSettings(context.getApplicationContext());
        View.inflate(context, R.layout.preference__themed_edittext, this);
        ButterKnife.bind(this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        // Assign custom attributes
        if (attrs != null) {
            String titleText = "";
            String summaryText = "";

            TypedArray a = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.ThemedStringEditTextPreference,
                    defStyle, 0);
            try {
                titleText = a.getString(R.styleable.ThemedStringEditTextPreference_titleText);
                summaryText = a.getString(R.styleable.ThemedStringEditTextPreference_summaryText);
                prefKey = a.getString(R.styleable.ThemedStringEditTextPreference_prefKey);
                defaultValue = a.getString(R.styleable.ThemedStringEditTextPreference_defaultString);
                showValueInSummary = a.getBoolean(R.styleable.ThemedStringEditTextPreference_showValueInSummary, false);
            } catch (Exception e) {
                AppLog.e(this, "There was an error loading attributes.");
            } finally {
                a.recycle();
            }
            final String finalTitle = titleText;

            setTitleText(titleText);
            setSummaryText(showValueInSummary ? appSettings.getThemedStringEditTextPreferenceValue(this) : summaryText);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(context, finalTitle);
                }
            });
        }
    }

    public void showDialog(Context context, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText dialogLayout = (EditText) LayoutInflater.from(context).inflate(R.layout.settings_activity__dialog_proxy, null, false);
        dialogLayout.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogLayout.setText(appSettings.getThemedStringEditTextPreferenceValue(this));
        builder.setTitle(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setValue(dialogLayout.getText().toString());
                        if(onPositiveButtonClickedListener != null) {
                            onPositiveButtonClickedListener.onPositiveButtonClicked(dialogLayout.getText().toString());
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null)
                .setView(dialogLayout)
                .show();
    }

    public void setTitleText(String titleText) {
        this.title.setText(titleText);
        if(titleText == null || titleText.equals("")) {
            title.setVisibility(GONE);
        } else {
            title.setVisibility(VISIBLE);
        }
    }

    public void setSummaryText(String summaryText) {
        this.summary.setText(summaryText);
        if(summaryText == null || summaryText.equals("")) {
            summary.setVisibility(GONE);
        } else {
            title.setVisibility(VISIBLE);
        }
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getValue() {
        return appSettings.getThemedStringEditTextPreferenceValue(this);
    }

    @Override
    public void setValue(String value) {
        appSettings.setThemedStringEditTextPreferenceValue(this, value);
        if(showValueInSummary) {
            setSummaryText(value);
        }
    }

    public String getPrefKey() {
        return this.prefKey;
    }

    public void setOnPositiveButtonClickedListener(OnPositiveButtonClickedListener listener) {
        this.onPositiveButtonClickedListener = listener;
    }

    public interface OnPositiveButtonClickedListener {
        void onPositiveButtonClicked(String input);
    }
}
