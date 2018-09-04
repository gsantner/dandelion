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
package com.github.dfa.diaspora_android.web;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.dfa.diaspora_android.BuildConfig;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.util.AppSettings;

import net.gsantner.opoc.util.DownloadTask;
import net.gsantner.opoc.util.PermissionChecker;
import net.gsantner.opoc.util.ShareUtil;

import java.io.File;
import java.util.Date;

/**
 * Subclass of WebView which adds a context menu for long clicks on images or links to share, save
 * or open with another browser
 */
@SuppressWarnings("deprecation")
public class ContextMenuWebView extends NestedWebView {

    public static final int ID_SAVE_IMAGE = 10;
    public static final int ID_IMAGE_EXTERNAL_BROWSER = 11;
    public static final int ID_COPY_IMAGE_LINK = 15;
    public static final int ID_COPY_LINK = 12;
    public static final int ID_SHARE_LINK = 13;
    public static final int ID_SHARE_IMAGE = 14;

    private final Context context;
    private Activity parentActivity;
    private String lastLoadUrl = "";

    public ContextMenuWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public ContextMenuWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);

        HitTestResult result = getHitTestResult();

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                HitTestResult result = getHitTestResult();
                String url = result.getExtra();
                final ShareUtil shu = new ShareUtil(context).setFileProviderAuthority(BuildConfig.APPLICATION_ID);
                final PermissionChecker permc = new PermissionChecker(parentActivity);
                final AppSettings appSettings = new AppSettings(context);

                switch (item.getItemId()) {
                    //Save image to external memory
                    case ID_SAVE_IMAGE: {
                        if (permc.doIfExtStoragePermissionGranted(context.getString(R.string.image_permission_description__appspecific))) {
                            File fileSaveDirectory = appSettings.getAppSaveDirectory();
                            if (permc.mkdirIfStoragePermissionGranted(fileSaveDirectory)) {
                                String filename = "dandelion-" + ShareUtil.SDF_SHORT.format(new Date()) + url.substring(url.lastIndexOf("."));
                                /*Uri source = Uri.parse(url);
                                DownloadManager.Request request = new DownloadManager.Request(source);
                                request.setDestinationUri(Uri.fromFile(new File(fileSaveDirectory, filename)));
                                ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);*/
                                new DownloadTask(new File(fileSaveDirectory, filename), (ok, dlfile) -> {
                                    if (ok) {
                                        Toast.makeText(context, context.getText(R.string.saving_image_to) + " " + dlfile.getName(), Toast.LENGTH_LONG).show();
                                    }
                                }).execute(url);
                            }
                        }
                        break;
                    }

                    case ID_SHARE_IMAGE: {
                        if (permc.doIfExtStoragePermissionGranted(context.getString(R.string.image_permission_description__appspecific))) {
                            File fileSaveDirectory = appSettings.getAppSaveDirectory();
                            if (permc.mkdirIfStoragePermissionGranted(fileSaveDirectory)) {
                                String filename = ".dandelion-shared" + url.substring(url.lastIndexOf("."));
                                new DownloadTask(new File(fileSaveDirectory, filename), (ok, dlfile) -> {
                                    if (ok) {
                                        Toast.makeText(context, context.getText(R.string.saving_image_to) + " " + dlfile.getName(), Toast.LENGTH_LONG).show();
                                        shu.shareStream(dlfile, "image/" + dlfile.getAbsolutePath().lastIndexOf(".") + 1);
                                    }
                                }).execute(url);
                            }
                        }
                        break;
                    }

                    case ID_IMAGE_EXTERNAL_BROWSER:
                        if (url != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            context.startActivity(intent);
                        }
                        break;

                    //Copy url to clipboard
                    case ID_COPY_IMAGE_LINK:
                    case ID_COPY_LINK:
                        if (url != null) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setPrimaryClip(ClipData.newPlainText("text", url));
                            Toast.makeText(context, R.string.link_adress_copied, Toast.LENGTH_SHORT).show();
                        }
                        break;

                    //Try to share link to other apps
                    case ID_SHARE_LINK:
                        if (url != null) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                            sendIntent.setType("text/plain");
                            context.startActivity(Intent.createChooser(sendIntent, getResources()
                                    .getText(R.string.share_link_address)));
                        }
                        break;
                }
                return true;
            }
        };

        //Build context menu
        if (result.getType() == HitTestResult.IMAGE_TYPE ||
                result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            // Menu options for an image.
            menu.setHeaderTitle(result.getExtra());
            menu.add(0, ID_SAVE_IMAGE, 0, context.getString(R.string.save_image)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_IMAGE_EXTERNAL_BROWSER, 0, context.getString(R.string.open_in_external_browser)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_SHARE_IMAGE, 0, context.getString(R.string.share_image)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_COPY_IMAGE_LINK, 0, context.getString(R.string.copy_image_address_to_clipboard)).setOnMenuItemClickListener(handler);
        } else if (result.getType() == HitTestResult.ANCHOR_TYPE ||
                result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
            // Menu options for a hyperlink.
            menu.setHeaderTitle(result.getExtra());
            menu.add(0, ID_COPY_LINK, 0, context.getString(R.string.copy_link_adress_to_clipboard)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_SHARE_LINK, 0, context.getString(R.string.share_link_address)).setOnMenuItemClickListener(handler);
        }
    }

    public void loadUrlNew(String url) {
        stopLoading();
        loadUrl(url);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        WebHelper.sendUpdateTitleByUrlIntent(url, getContext());
    }

    public void setParentActivity(Activity activity) {
        this.parentActivity = activity;
    }
}
