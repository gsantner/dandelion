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
package com.github.dfa.diaspora_android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;

public class BadgeDrawable extends Drawable {
    // Source: http://mobikul.com/adding-badge-count-on-menu-items-like-cart-notification-etc/
    private static final String BADGE_VALUE_OVERFLOW = "*";

    private Paint badgeBackground;
    private Paint badgeStroke;
    private Paint badgeText;
    private Rect textRect = new Rect();

    private String badgeValue = "";
    private boolean shouldDraw;

    public BadgeDrawable(Context context) {
        float textSize = context.getResources().getDimension(R.dimen.textsize_badge_count);

        AppSettings settings = new AppSettings(context);
        badgeBackground = new Paint();
        badgeBackground.setColor(settings.getAccentColor());
        badgeBackground.setAntiAlias(true);
        badgeBackground.setStyle(Paint.Style.FILL);
        badgeStroke = new Paint();
        badgeStroke.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorPrimaryDark));
        badgeStroke.setAntiAlias(true);
        badgeStroke.setStyle(Paint.Style.FILL);

        badgeText = new Paint();
        badgeText.setColor(Color.WHITE);
        badgeText.setTypeface(Typeface.DEFAULT);
        badgeText.setTextSize(textSize);
        badgeText.setAntiAlias(true);
        badgeText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!shouldDraw) {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        // Position the badge in the top-right quadrant of the icon.
        float radius = ((Math.max(width, height) / 2)) / 2;
        float centerX = (width - radius - 1) + 5;
        float centerY = radius - 5;
        if (badgeValue.length() <= 2) {
            // Draw badge circle.
            canvas.drawCircle(centerX, centerY, (int) (radius + 7.5), badgeStroke);
            canvas.drawCircle(centerX, centerY, (int) (radius + 5.5), badgeBackground);
        } else {
            canvas.drawCircle(centerX, centerY, (int) (radius + 8.5), badgeStroke);
            canvas.drawCircle(centerX, centerY, (int) (radius + 6.5), badgeBackground);
            //canvas.drawRoundRect(radius, radius, radius, radius, 10, 10, badgeBackground);
        }
        // Draw badge count text inside the circle.
        badgeText.getTextBounds(badgeValue, 0, badgeValue.length(), textRect);
        float textHeight = textRect.bottom - textRect.top;
        float textY = centerY + (textHeight / 2f);
        if (badgeValue.length() > 2)
            canvas.drawText(BADGE_VALUE_OVERFLOW, centerX, textY, badgeText);
        else
            canvas.drawText(badgeValue, centerX, textY, badgeText);
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    public void setCount(String count) {
        badgeValue = count;

        // Only draw a badge if there are notifications.
        shouldDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        // do nothing
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, Integer count) {
        setBadgeCount(context, icon, count.toString());
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
}