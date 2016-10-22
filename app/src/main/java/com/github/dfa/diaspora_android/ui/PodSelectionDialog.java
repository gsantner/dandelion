package com.github.dfa.diaspora_android.ui;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.data.DiasporaPodList.DiasporaPod;
import com.github.dfa.diaspora_android.data.DiasporaPodList.DiasporaPod.DiasporaPodUrl;
import com.github.dfa.diaspora_android.util.ProxyHandler;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by gsantner (https://gsantner.github.io) on 06.10.16.
 */
public class PodSelectionDialog extends AppCompatDialogFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.PodSelectionDialog";

    public static interface PodSelectionDialogResultListener {
        void onPodSelectionDialogResult(DiasporaPod pod, boolean accepted);
    }

    public static PodSelectionDialog newInstance(PodSelectionDialogResultListener resultListener) {
        return newInstance(new DiasporaPod(), resultListener);
    }

    public static PodSelectionDialog newInstance(DiasporaPod pod, PodSelectionDialogResultListener resultListener) {
        PodSelectionDialog dialog = new PodSelectionDialog();
        dialog.setPod(pod);
        dialog.setResultListener(resultListener);
        return dialog;
    }

    /*
    //    ██████╗ ██╗ █████╗ ██╗      ██████╗  ██████╗
    //    ██╔══██╗██║██╔══██╗██║     ██╔═══██╗██╔════╝
    //    ██║  ██║██║███████║██║     ██║   ██║██║  ███╗
    //    ██║  ██║██║██╔══██║██║     ██║   ██║██║   ██║
    //    ██████╔╝██║██║  ██║███████╗╚██████╔╝╚██████╔╝
    //    ╚═════╝ ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝
    */

    @BindView(R.id.podselection__dialog__edit_podaddress)
    EditText editPodAddress;

    @BindView(R.id.podselection__dialog__edit_podname)
    EditText editPodName;

    @BindView(R.id.podselection__dialog__radiogroup_protocol)
    RadioGroup radiogrpProtocol;

    @BindView(R.id.podselection__dialog__text_profile)
    TextView textProfile;

    @BindView(R.id.podselection__dialog__spinner_profile)
    Spinner spinnerProfile;

    @BindView(R.id.podselection__dialog__check_torpreset)
    CheckBox checkboxTorPreset;

    @BindView(R.id.podselection__dialog__text_torpreset)
    TextView textTorPreset;

    private PodSelectionDialogResultListener resultListener;
    private View root;
    private DiasporaPod pod = new DiasporaPod();
    private App app;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        app = (App) getActivity().getApplication();

        // Bind UI
        root = inflater.inflate(R.layout.podselection__dialog, null);
        ButterKnife.bind(this, root);
        editPodName.setText(pod.getName());
        List<DiasporaPodUrl> podUrls = pod.getPodUrls();
        if (podUrls.size() > 0) {
            uiLoadDiasporaUrl(0);
        }
        if (podUrls.size() > 1) {
            textProfile.setVisibility(View.VISIBLE);
            spinnerProfile.setVisibility(View.VISIBLE);
            String[] podUrlss = new String[podUrls.size()];
            for (int i = 0; i < podUrls.size(); podUrlss[i] = podUrls.get(i++).getBaseUrl()) ;
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, podUrlss);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProfile.setAdapter(spinnerAdapter);
        }

        builder.setView(root);
        return builder.create();
    }

    @OnItemSelected(R.id.podselection__dialog__spinner_profile)
    public void spinnerItemSelected(Spinner spinner, int position) {
        uiLoadDiasporaUrl(position);
    }

    public void uiLoadDiasporaUrl(int wantedPodUrlPos) {
        List<DiasporaPodUrl> podUrls = pod.getPodUrls();
        if (podUrls.size() == 0) {
            return;
        }
        wantedPodUrlPos = wantedPodUrlPos < podUrls.size() ? wantedPodUrlPos : 0;

        DiasporaPodUrl url1 = podUrls.get(wantedPodUrlPos);
        editPodAddress.setText(url1.getHost());
        radiogrpProtocol.check(url1.getProtocol().equals("https")
                ? R.id.podselection__dialog__radio_https : R.id.podselection__dialog__radio_http);

        // Tor
        boolean isOnionUrl = url1.getHost().endsWith(".onion");
        setUiVisible(textTorPreset, isOnionUrl);
        setUiVisible(checkboxTorPreset, isOnionUrl);
        checkboxTorPreset.setChecked(isOnionUrl);
    }

    public void setUiVisible(View view, boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }


    @OnClick({R.id.podselection__dialog__btn_ok, R.id.podselection__dialog__btn_cancel})
    public void onResultButtonClicked(View view) {
        boolean POSITIVE_PRESSED = view.getId() == R.id.podselection__dialog__btn_ok;
        if (POSITIVE_PRESSED) {
            if (!checkInputs()) {
                return;
            }
            DiasporaPodUrl podUrl = new DiasporaPodUrl();
            if (radiogrpProtocol.getCheckedRadioButtonId() == R.id.podselection__dialog__radio_https) {
                podUrl.setHttpsDefaults();
            } else {
                podUrl.setHttpDefaults();
            }
            podUrl.setHost(editPodAddress.getText().toString());
            pod.setName(editPodName.getText().toString());
            pod.getPodUrls().clear();
            pod.getPodUrls().add(podUrl);

            // Load Tor preset
            if(pod.getPodUrl().getHost().endsWith(".onion") && checkboxTorPreset.isChecked()){
                AppSettings settings = app.getSettings();
                settings.setProxyHttpEnabled(true);
                settings.setProxyHttpPort(8118);
                settings.setProxyHttpHost("127.0.0.1");
                ProxyHandler.getInstance().updateProxySettings(getContext());
            }

            getDialog().dismiss();
            publishResult(true);
        } else {
            getDialog().cancel();
            publishResult(false);
        }
    }

    public boolean checkInputs() {
        boolean ok = true;
        String s = editPodAddress.getText().toString();
        if (TextUtils.isEmpty(s) || s.length() < 3) {
            editPodAddress.setError(getString(R.string.missing_value));
            ok = false;
        }
        s = editPodName.getText().toString();
        if (TextUtils.isEmpty(s) || s.length() < 3) {
            editPodName.setError(getString(R.string.missing_value));
            ok = false;
        }
        return ok;
    }

    public void publishResult(boolean accepted) {
        if (resultListener != null) {
            resultListener.onPodSelectionDialogResult(pod, accepted);
        }
    }

    /*
     * GETTER & SETTER
     */
    public PodSelectionDialogResultListener getResultListener() {
        return resultListener;
    }

    public void setResultListener(PodSelectionDialogResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public DiasporaPod getPod() {
        return pod;
    }

    public void setPod(DiasporaPod pod) {
        try {
            this.pod = new DiasporaPod().fromJson(pod.toJson());
        } catch (JSONException ignored) {
        }
    }
}
