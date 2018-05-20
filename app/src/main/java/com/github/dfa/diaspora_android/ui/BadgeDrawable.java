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
import android.support.annotation.NonNull;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.ContextUtils;

@SuppressWarnings("WeakerAccess")
public class BadgeDrawable extends Drawable {
    // Source: http://mobikul.com/adding-badge-count-on-menu-items-like-cart-notification-etc/
    private static final String BADGE_VALUE_OVERFLOW = "*";

    private Paint _badgeBackground;
    private Paint _badgeText;
    private Rect _textRect = new Rect();

    private String _badgeValue = "";
    private boolean _shouldDraw;

    public BadgeDrawable(Context context) {
        float textSize = context.getResources().getDimension(R.dimen.textsize_badge_count);

        AppSettings settings = AppSettings.get();
        _badgeBackground = new Paint();
        _badgeBackground.setColor(settings.getAccentColor());
        _badgeBackground.setAntiAlias(true);
        _badgeBackground.setStyle(Paint.Style.FILL);

        _badgeText = new Paint();
        _badgeText.setColor(ContextUtils.get().shouldColorOnTopBeLight(settings.getAccentColor()) ? Color.WHITE : Color.BLACK);
        _badgeText.setTypeface(Typeface.DEFAULT);
        _badgeText.setTextSize(textSize);
        _badgeText.setAntiAlias(true);
        _badgeText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (!_shouldDraw) {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;
        float oneDp = ContextUtils.get().convertDpToPx(1);

        // Position the badge in the top-right quadrant of the icon.
        float radius = ((Math.max(width, height) / 2)) / 2;
        float centerX = (width - radius - 1) + oneDp * 2;
        float centerY = radius - 2 * oneDp;
        canvas.drawCircle(centerX, centerY, (int) (radius + oneDp * 5), _badgeBackground);

        // Draw badge count message inside the circle.
        _badgeText.getTextBounds(_badgeValue, 0, _badgeValue.length(), _textRect);
        float textHeight = _textRect.bottom - _textRect.top;
        float textY = centerY + (textHeight / 2f);
        canvas.drawText(_badgeValue.length() > 2 ? BADGE_VALUE_OVERFLOW : _badgeValue,
                centerX, textY, _badgeText);
    }

    // Sets the text to display. Badge displays a '*' if more than 2 characters
    private void setBadgeText(String text) {
        _badgeValue = text;

        // Only draw a badge if the value isn't a zero
        _shouldDraw = !text.equalsIgnoreCase("0");
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, Integer count) {
        setBadgeText(context, icon, count.toString());
    }

    // Max of 2 characters
    public static void setBadgeText(Context context, LayerDrawable icon, String text) {
        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setBadgeText(text);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
}