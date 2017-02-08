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

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.listener.OnSomethingClickListener;
import com.github.dfa.diaspora_android.ui.theme.ThemedFragment;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;
import com.github.dfa.diaspora_android.util.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment that shows a list of the HashTags the user follows
 */
public class TagListFragment extends ThemedFragment implements OnSomethingClickListener<Object> {

    public static final String TAG = "com.github.dfa.diaspora_android.TagListFragment";

    protected RecyclerView followedTagsRecyclerView;
    protected App app;
    protected DiasporaUrlHelper urls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        return inflater.inflate(R.layout.recycler_list__fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        followedTagsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_list__recycler_view);
        app = (App) getActivity().getApplication();
        AppSettings appSettings = app.getSettings();
        urls = new DiasporaUrlHelper(appSettings);

        followedTagsRecyclerView.setHasFixedSize(true);
        followedTagsRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        followedTagsRecyclerView.setLayoutManager(layoutManager);

        final FollowedTagsAdapter adapter = new FollowedTagsAdapter(appSettings, this);
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

    @Override
    public void onSomethingClicked(Object null1, Integer null2, String tag) {
        ((MainActivity) getActivity()).openDiasporaUrl(urls.getSearchTagsUrl(tag));
    }

    @Override
    protected void applyColorToViews() {
        followedTagsRecyclerView.invalidate();
    }

    public static class FollowedTagsAdapter extends RecyclerView.Adapter<FollowedTagsAdapter.ViewHolder> {
        private AppSettings appSettings;
        private String[] followedTagsList;
        private List<String> followedTagsFavsList;
        private OnSomethingClickListener<Object> tagClickedListener;

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.recycler_view__list_item__text)
            public TextView title;
            @BindView(R.id.recycler_view__list_item__favourite)
            AppCompatImageView favouriteImage;
            @BindView(R.id.recycler_view__list_item__root)
            RelativeLayout root;

            ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
            }
        }


        FollowedTagsAdapter(AppSettings appSettings, OnSomethingClickListener<Object> tagClickedListener) {
            this.appSettings = appSettings;
            this.followedTagsList = appSettings.getFollowedTags();
            this.followedTagsFavsList = new ArrayList<>(Arrays.asList(appSettings.getFollowedTagsFavs()));
            this.tagClickedListener = tagClickedListener;
        }

        @Override
        public int getItemCount() {
            return followedTagsList.length;
        }

        @Override
        public FollowedTagsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_list__list_item_with_fav, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // Alternating colors
            final Context c = holder.root.getContext();
            final String tag = followedTagsList[position];
            holder.title.setText(tag);
            if (position % 2 == 1) {
                holder.root.setBackgroundColor(Helpers.getColorFromRessource(c, R.color.alternate_row_color));
            }

            // Favourite (Star) Image
            applyFavouriteImage(holder.favouriteImage, isFollowedTagFaved(tag));

            // Click on fav button
            holder.favouriteImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (isFollowedTagFaved(tag)) {
                        followedTagsFavsList.remove(followedTagsFavsList.indexOf(tag));
                    } else {
                        followedTagsFavsList.add(tag);
                    }
                    appSettings.setFollowedTagsFavs(followedTagsFavsList);
                    applyFavouriteImage(holder.favouriteImage, isFollowedTagFaved(tag));
                }
            });

            holder.root.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    tagClickedListener.onSomethingClicked(null, null, tag);
                }
            });
        }

        private boolean isFollowedTagFaved(String tag) {
            return followedTagsFavsList.contains(tag);
        }

        private void applyFavouriteImage(AppCompatImageView imageView, boolean isFaved) {
            imageView.setImageResource(isFaved ? R.drawable.ic_star_filled_48px : R.drawable.ic_star_border_black_48px);
            imageView.setColorFilter(isFaved ? appSettings.getAccentColor() : 0, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
