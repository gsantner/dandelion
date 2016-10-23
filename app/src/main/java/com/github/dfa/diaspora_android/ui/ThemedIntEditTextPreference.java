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

public class ThemedIntEditTextPreference extends LinearLayout implements ThemedPreference<Integer> {
    @BindView(R.id.preference__themed_edittext_title)
    protected TextView title;
    @BindView(R.id.preference__themed_edittext_summary)
    protected TextView summary;

    protected String prefKey;
    protected int defaultValue;
    protected AppSettings appSettings;
    protected boolean showValueInSummary;
    protected OnPositiveButtonClickedListener onPositiveButtonClickedListener;

    public ThemedIntEditTextPreference(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ThemedIntEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ThemedIntEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
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
                    R.styleable.ThemedIntEditTextPreference,
                    defStyle, 0);
            try {
                titleText = a.getString(R.styleable.ThemedIntEditTextPreference_titleText);
                summaryText = a.getString(R.styleable.ThemedIntEditTextPreference_summaryText);
                prefKey = a.getString(R.styleable.ThemedIntEditTextPreference_prefKey);
                defaultValue = a.getInt(R.styleable.ThemedIntEditTextPreference_defaultInt, 0);
                showValueInSummary = a.getBoolean(R.styleable.ThemedIntEditTextPreference_showValueInSummary, false);
            } catch (Exception e) {
                AppLog.e(this, "There was an error loading attributes.");
            } finally {
                a.recycle();
            }
            final String finalTitle = titleText;

            AppLog.d(this, "ShowValueInSummary: "+showValueInSummary + " port: "+appSettings.getProxyHttpPort());
            setTitleText(titleText);
            setSummaryText(showValueInSummary ? ""+appSettings.getThemedIntEditTextPreferenceValue(this) : summaryText);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppLog.d(this, "Click!");
                    showDialog(context, finalTitle);
                }
            });
        }
    }

    public void showDialog(Context context, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText dialogLayout = (EditText) LayoutInflater.from(context).inflate(R.layout.settings_activity__dialog_proxy, null, false);
        dialogLayout.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialogLayout.setText(""+appSettings.getThemedIntEditTextPreferenceValue(this));
        builder.setTitle(title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setValue(Integer.valueOf(dialogLayout.getText().toString()));
                        if(onPositiveButtonClickedListener != null) {
                            onPositiveButtonClickedListener.onPositiveButtonClicked(Integer.valueOf(dialogLayout.getText().toString()));
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
    public Integer getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Integer getValue() {
        return appSettings.getThemedIntEditTextPreferenceValue(this);
    }

    @Override
    public void setValue(Integer value) {
        appSettings.setThemedIntEditTextPreferenceValue(this, value);
        if(showValueInSummary) {
            setSummaryText(""+value);
        }
    }

    public String getPrefKey() {
        return this.prefKey;
    }

    public void setOnPositiveButtonClickedListener(OnPositiveButtonClickedListener listener) {
        this.onPositiveButtonClickedListener = listener;
    }

    public interface OnPositiveButtonClickedListener {
        void onPositiveButtonClicked(int input);
    }
}
