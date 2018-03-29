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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.service.ImageDownloadTask;
import com.github.dfa.diaspora_android.util.ActivityUtils;

import java.io.File;

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
                switch (item.getItemId()) {
                    //Save image to external memory
                    case ID_SAVE_IMAGE: {
                        boolean writeToStoragePermitted = true;
                        if (android.os.Build.VERSION.SDK_INT >= 23) {
                            int hasWRITE_EXTERNAL_STORAGE = parentActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                                writeToStoragePermitted = false;
                                if (!parentActivity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    new AlertDialog.Builder(parentActivity)
                                            .setMessage(R.string.permissions_image)
                                            .setPositiveButton(context.getText(android.R.string.yes), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (android.os.Build.VERSION.SDK_INT >= 23)
                                                        parentActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                                MainActivity.REQUEST_CODE__ACCESS_EXTERNAL_STORAGE);
                                                }
                                            })
                                            .setCancelable(false)
                                            .setNegativeButton(context.getText(android.R.string.no), null)
                                            .show();
                                }
                                parentActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MainActivity.REQUEST_CODE__ACCESS_EXTERNAL_STORAGE);
                            }
                        }
                        if (writeToStoragePermitted) {
                            //Make sure, Diaspora Folder exists
                            File destinationFolder = new File(Environment.getExternalStorageDirectory() + "/Pictures/Diaspora");
                            if (!destinationFolder.exists()) {
                                destinationFolder.mkdirs();
                            }

                            if (url != null) {
                                Uri source = Uri.parse(url);
                                DownloadManager.Request request = new DownloadManager.Request(source);
                                File destinationFile = new File(Environment.getExternalStorageDirectory() + "/Pictures/Diaspora/" + System.currentTimeMillis() + ".png");

                                request.setDestinationUri(Uri.fromFile(destinationFile));
                                ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);

                                Toast.makeText(context, context.getText(R.string.share__toast_saved_image_to_location) + " " +
                                        destinationFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    break;

                    case ID_SHARE_IMAGE:
                        if (url != null) {
                            boolean writeToStoragePermitted = true;
                            if (android.os.Build.VERSION.SDK_INT >= 23) {
                                int hasWRITE_EXTERNAL_STORAGE = parentActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                                    writeToStoragePermitted = false;
                                    if (!parentActivity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                        new AlertDialog.Builder(parentActivity)
                                                .setMessage(R.string.permissions_image)
                                                .setPositiveButton(context.getText(android.R.string.yes), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (android.os.Build.VERSION.SDK_INT >= 23)
                                                            parentActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                                    MainActivity.REQUEST_CODE__ACCESS_EXTERNAL_STORAGE);
                                                    }
                                                })
                                                .setNegativeButton(context.getText(android.R.string.no), null)
                                                .show();
                                    } else {
                                        parentActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                MainActivity.REQUEST_CODE__ACCESS_EXTERNAL_STORAGE);
                                    }
                                }
                            }
                            if (writeToStoragePermitted) {
                                //Make sure, Diaspora Folder exists
                                File destinationFolder = new File(Environment.getExternalStorageDirectory() + "/Pictures/Diaspora");
                                if (!destinationFolder.exists()) {
                                    destinationFolder.mkdirs();
                                }

                                final Uri local = Uri.parse(Environment.getExternalStorageDirectory() + "/Pictures/Diaspora/" + System.currentTimeMillis() + ".png");
                                new ImageDownloadTask(null, local.getPath()) {
                                    @Override
                                    protected void onPostExecute(Bitmap result) {

                                        Uri myUri = ActivityUtils.getFileSharingUri(context, new File(local.getPath()));
                                        Intent sharingIntent = new Intent();
                                        sharingIntent.setAction(Intent.ACTION_SEND);
                                        sharingIntent.putExtra(Intent.EXTRA_STREAM, myUri);
                                        sharingIntent.setType("image/png");
                                        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        context.startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_share_dotdotdot)));
                                    }
                                }.execute(url);
                            }
                        } else {
                            Toast.makeText(context, "Cannot share image: url is null", Toast.LENGTH_SHORT).show();
                        }

                        break;

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
                            Toast.makeText(context, R.string.share__toast_link_address_copied, Toast.LENGTH_SHORT).show();
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
                                    .getText(R.string.context_menu_share_link)));
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
            menu.add(0, ID_SAVE_IMAGE, 0, context.getString(R.string.context_menu_save_image)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_IMAGE_EXTERNAL_BROWSER, 0, context.getString(R.string.context_menu_open_external_browser)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_SHARE_IMAGE, 0, context.getString(R.string.context_menu_share_image)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_COPY_IMAGE_LINK, 0, context.getString(R.string.context_menu_copy_image_link)).setOnMenuItemClickListener(handler);
        } else if (result.getType() == HitTestResult.ANCHOR_TYPE ||
                result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
            // Menu options for a hyperlink.
            menu.setHeaderTitle(result.getExtra());
            menu.add(0, ID_COPY_LINK, 0, context.getString(R.string.context_menu_copy_link)).setOnMenuItemClickListener(handler);
            menu.add(0, ID_SHARE_LINK, 0, context.getString(R.string.context_menu_share_link)).setOnMenuItemClickListener(handler);
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
