/*
    This file is part of the Diaspora for Android.

    Diaspora for Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Diaspora for Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora for Android.

    If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dfa.diaspora_android.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;

/**
 * Fragment that shows a list of the Hashtags the user follows
 * Created by vanitas on 29.09.16.
 */

public class HashtagListFragment extends CustomFragment {

    public static final String TAG = "com.github.dfa.diaspora_android.HashtagListFragment";

    protected RecyclerView followedTagsRecyclerView;
    protected String[] followedTags;
    protected App app;
    protected DiasporaUrlHelper urls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        return inflater.inflate(R.layout.hashtag_list__fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.followedTagsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_followed_tags__recycler_view);
        this.app = (App) getActivity().getApplication();
        this.urls = new DiasporaUrlHelper(app.getSettings());

        followedTags = app.getPodUserProfile().getFollowedTags();
        followedTagsRecyclerView.setHasFixedSize(true);
        followedTagsRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        followedTagsRecyclerView.setLayoutManager(layoutManager);

        final FollowedTagsAdapter adapter = new FollowedTagsAdapter(followedTags, onHashtagClickListener);
        followedTagsRecyclerView.setAdapter(adapter);

        //Set window title
        getActivity().setTitle(R.string.nav_followed_tags);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onCreateBottomOptionsMenu(Menu menu, MenuInflater inflater) {
        /* Nothing to do */
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    protected View.OnClickListener onHashtagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int itemPosition = followedTagsRecyclerView.getChildLayoutPosition(view);
            if (itemPosition > -1 && itemPosition < followedTags.length) {
                String tag = followedTags[itemPosition];
                ((MainActivity) getActivity()).openDiasporaUrl(urls.getSearchTagsUrl(tag));
            }
        }
    };

    public static class FollowedTagsAdapter extends RecyclerView.Adapter<FollowedTagsAdapter.ViewHolder> {
        private String[] followedTagsList;
        private View.OnClickListener itemClickListener;

        static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView title;

            ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.recycler_view__list_item__text);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        FollowedTagsAdapter(String[] tags, View.OnClickListener itemClickListener) {
            this.followedTagsList = tags;
            this.itemClickListener = itemClickListener;
        }

        @Override
        public FollowedTagsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view__list_item, parent, false);
            v.setOnClickListener(itemClickListener);
            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.title.setText(followedTagsList[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return followedTagsList.length;
        }
    }
}
