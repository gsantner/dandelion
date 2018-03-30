/*
    This file is part of the dandelion*.

    dandelion* is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    dandelion* is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the dandelion*.

    If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dfa.diaspora_android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.DiasporaPodList;
import com.github.dfa.diaspora_android.data.DiasporaPodList.DiasporaPod;
import com.github.dfa.diaspora_android.service.FetchPodsService;
import com.github.dfa.diaspora_android.ui.PodSelectionDialog;
import com.github.dfa.diaspora_android.ui.theme.ThemedFragment;
import com.github.dfa.diaspora_android.util.ActivityUtils;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.ContextUtils;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment that lets the user choose a Pod
 * Created by vanitas on 01.10.16.
 */

public class PodSelectionFragment extends ThemedFragment implements SearchView.OnQueryTextListener, PodSelectionDialog.PodSelectionDialogResultListener {
    public static final String TAG = "com.github.dfa.diaspora_android.PodSelectionFragment";

    @BindView(R.id.podselection__fragment__listpods)
    protected ListView listViewPod;

    @BindView(R.id.podselection__fragment__root)
    RelativeLayout rootView;

    @BindView(R.id.podselection__fragment__button_use_custom_pod)
    AppCompatButton buttonUseCustomPod;


    protected App app;
    protected AppSettings appSettings;
    private DiasporaPodList podList;
    private ArrayAdapter<String> listViewPodAdapter;
    private String filterString = "";

    @Override
    protected int getLayoutResId() {
        return R.layout.podselection__fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        app = (App) getActivity().getApplication();
        appSettings = app.getSettings();

        // Load local podlist
        podList = new DiasporaPodList();
        mergePodlistWithRessources(podList);
        podList.setTrackMergeChanges(true);
        updateListedPods();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listViewPod.setNestedScrollingEnabled(true);
        }

        listViewPod.setTextFilterEnabled(true);
        listViewPod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = ((TextView) view).getText().toString();
                for (DiasporaPod pod : podList) {
                    if (pod.getPodUrl().getHost().equals(text)) {
                        showPodSelectionDialog(pod);
                        return;
                    }
                }

            }
        });
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(podListReceiver, new IntentFilter(FetchPodsService.MESSAGE_PODS_RECEIVED));
        ActivityUtils.get(getActivity()).showInfoIfUserNotConnectedToInternet(listViewPod);
    }

    public void mergePodlistWithRessources(DiasporaPodList podlist) {
        String sPodlist = ContextUtils.get().readTextfileFromRawRes(R.raw.podlist, "", "");
        try {
            JSONObject jPodlist = new JSONObject(sPodlist);
            podlist.mergeWithNewerEntries(new DiasporaPodList().fromJson(jPodlist));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.podselection__fragment__button_use_custom_pod)
    public void onPodButtonClicked(View v) {
        showPodSelectionDialog(new DiasporaPod());
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    private final BroadcastReceiver podListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(FetchPodsService.EXTRA_PODLIST)) {
                Bundle extras = intent.getExtras();
                DiasporaPodList newPods = (DiasporaPodList) extras.get(FetchPodsService.EXTRA_PODLIST);
                if (newPods != null && newPods.getPods().size() > 0) {
                    try {
                        podList.mergeWithNewerEntries(newPods);
                        updateListedPods();
                    } catch (JSONException ignored) {
                    }
                } else {
                    Snackbar.make(listViewPod, R.string.podlist_error, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void applyColorToViews() {
        int dividerHeight = listViewPod.getDividerHeight();
        rootView.setBackgroundColor(appSettings.isAmoledColorMode() ? Color.BLACK : Color.WHITE);
        listViewPod.setDivider(new ColorDrawable(Color.GRAY));
        listViewPod.setDividerHeight(dividerHeight);
        if (appSettings.isAmoledColorMode()) {
            buttonUseCustomPod.setTextColor(Color.WHITE);
        } else {
            buttonUseCustomPod.setTextColor(ContextUtils.get().shouldColorOnTopBeLight(appSettings.getAccentColor()) ? Color.WHITE : Color.BLACK);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i = new Intent(getContext(), FetchPodsService.class);
        getContext().startService(i);
    }

    private void updateListedPods() {
        final ArrayList<String> listedPodsList = new ArrayList<>();
        for (DiasporaPod pod : this.podList) {
            listedPodsList.add(pod.getPodUrl().getHost());
        }

        listViewPodAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                listedPodsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(appSettings.isAmoledColorMode() ? Color.GRAY : Color.BLACK);
                return view;
            }
        };

        // save index and top position
        int index = listViewPod.getFirstVisiblePosition();
        View v = listViewPod.getChildAt(0);
        int top = (v == null) ? 0 : (v.getTop() - listViewPod.getPaddingTop());
        listViewPod.setAdapter(listViewPodAdapter);
        listViewPod.setSelectionFromTop(index, top);

        listViewPodAdapter.getFilter().filter(filterString);
    }

    private void showPodSelectionDialog(final DiasporaPod selectedPod) {
        PodSelectionDialog dialog = PodSelectionDialog.newInstance(selectedPod, this);
        dialog.show(getFragmentManager(), PodSelectionDialog.TAG);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(podListReceiver);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.podselection__menu, menu);

        MenuItem searchItem = menu.findItem(R.id.podselection__action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(this);
        }

        final boolean darkBg = ContextUtils.get().shouldColorOnTopBeLight(AppSettings.get().getPrimaryColor());
        ContextUtils.get().tintMenuItems(menu, true, ContextCompat.getColor(getActivity(), darkBg ? R.color.white : R.color.black));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload: {
                if (!ActivityUtils.get(getActivity()).showInfoIfUserNotConnectedToInternet(listViewPod)) {
                    Intent i = new Intent(getContext(), FetchPodsService.class);
                    getContext().startService(i);
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (listViewPodAdapter != null) {
            (listViewPodAdapter).getFilter().filter(newText);
        }
        return true;
    }

    @Override
    public void onPodSelectionDialogResult(DiasporaPod pod, boolean accepted) {
        System.out.println(accepted + ": " + pod.toString());
        if (accepted) {
            app.getSettings().setPod(pod);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().removeSessionCookies(null);
                } else {
                    //noinspection deprecation
                    CookieManager.getInstance().removeAllCookie();
                    //noinspection deprecation
                    CookieManager.getInstance().removeSessionCookie();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainActivity mainActivity = (MainActivity) getActivity();
            DiasporaUrlHelper urlHelper = new DiasporaUrlHelper(appSettings);
            mainActivity.onPodSelectionDialogResult(pod, accepted);
            mainActivity.openDiasporaUrl(urlHelper.getSignInUrl());
        }
    }


    /*
     *  Dummy implementations
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean isAllowedIntellihide() {
        return false;
    }
}