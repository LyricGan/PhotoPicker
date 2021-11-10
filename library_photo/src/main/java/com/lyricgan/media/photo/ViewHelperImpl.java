package com.lyricgan.media.photo;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lyricgan.media.photo.view.IImageLoader;

class ViewHelperImpl {
    private View view;

    public ViewHelperImpl() {
    }

    /**
     * create an instance of {@link ViewHelperImpl}
     *
     * @param target the target to view
     */
    public ViewHelperImpl(View target) {
        this.view = target;
    }

    /**
     * change the current view to the target
     */
    public ViewHelperImpl view(View target) {
        if (target == null)
            throw new NullPointerException("target view can;t be null!");
        this.view = target;
        return this;
    }

    /**
     * reverse to the  t
     *
     * @param t the object to reverse.
     * @return
     */
    public <T> T reverse(T t) {
        return t;
    }

    public Context getContext() {
        return view.getContext();
    }

    public ViewHelperImpl addTextChangedListener(TextWatcher watcher) {
        ((TextView) view).addTextChangedListener(watcher);
        return this;
    }

    public ViewHelperImpl setVisibility(boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public ViewHelperImpl setVisibility(int visibility) {
        view.setVisibility(visibility);
        return this;
    }

    public ViewHelperImpl setText(CharSequence text) {
        ((TextView) view).setText(text);
        return this;
    }

    public ViewHelperImpl setEnabled(boolean enable) {
        view.setEnabled(enable);
        return this;
    }

    public ViewHelperImpl toogleVisibility() {
        View view = this.view;
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public ViewHelperImpl setImageResource(int imageResId) {
        ((ImageView) view).setImageResource(imageResId);
        return this;
    }

    public ViewHelperImpl setBackgroundColor(int color) {
        view.setBackgroundColor(color);
        return this;
    }

    public ViewHelperImpl setBackgroundRes(int backgroundRes) {
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public ViewHelperImpl setBackgroundDrawable(Drawable d) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(d);
        } else {
            view.setBackgroundDrawable(d);
        }
        return this;
    }

    public ViewHelperImpl setTextAppearance(int redId) {
        ((TextView) view).setTextAppearance(view.getContext(), redId);
        return this;
    }

    public ViewHelperImpl setTextColor(int textColor) {
        ((TextView) view).setTextColor(textColor);
        return this;
    }

    public ViewHelperImpl setTextColor(ColorStateList colorList) {
        ((TextView) view).setTextColor(colorList);
        return this;
    }

    public ViewHelperImpl setTextColorRes(int textColorResId) {
        return setTextColor(getContext().getResources().getColor(textColorResId));
    }

    public ViewHelperImpl setTextColorStateListRes(int textColorStateListResId) {
        return setTextColor(getContext().getResources().getColorStateList(textColorStateListResId));
    }

    public ViewHelperImpl setImageDrawable(Drawable d) {
        ((ImageView) view).setImageDrawable(d);
        return this;
    }

    public ViewHelperImpl setImageUrl(String url, IImageLoader loader) {
        loader.load((ImageView) view, url);
        return this;
    }

    public ViewHelperImpl setImageBitmap(Bitmap bitmap) {
        ((ImageView) view).setImageBitmap(bitmap);
        return this;
    }

    public ViewHelperImpl setAlpha(float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setAlpha(value);
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(value, value);
            alphaAnimation.setDuration(0);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
        }
        return this;
    }

    public ViewHelperImpl linkify() {
        Linkify.addLinks((TextView) view, Linkify.ALL);
        return this;
    }

    /**
     * @see Linkify#addLinks(TextView, int)
     */
    public ViewHelperImpl linkify(int mask) {
        Linkify.addLinks((TextView) view, mask);
        return this;
    }

    public ViewHelperImpl setTypeface(Typeface typeface) {
        TextView view = (TextView) this.view;
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    public ViewHelperImpl setProgress(int progress) {
        ((ProgressBar) view).setProgress(progress);
        return this;
    }

    public ViewHelperImpl setProgress(int progress, int max) {
        ((ProgressBar) view).setProgress(progress);
        ((ProgressBar) view).setMax(max);
        return this;
    }

    public ViewHelperImpl setProgressMax(int max) {
        ((ProgressBar) view).setMax(max);
        return this;
    }

    public ViewHelperImpl setRating(float rating) {
        ((RatingBar) view).setRating(rating);
        return this;
    }

    public ViewHelperImpl setRating(float rating, int max) {
        ((RatingBar) view).setRating(rating);
        ((RatingBar) view).setMax(max);
        return this;
    }

    public ViewHelperImpl setTag(Object tag) {
        view.setTag(tag);
        return this;
    }

    public ViewHelperImpl setTag(int key, Object tag) {
        view.setTag(key, tag);
        return this;
    }

    public ViewHelperImpl setChecked(boolean checked) {
        ((Checkable) view).setChecked(checked);
        return this;
    }

    public ViewHelperImpl setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener l) {
        ((CompoundButton) view).setOnCheckedChangeListener(l);
        return this;
    }

    public ViewHelperImpl setOnClickListener(View.OnClickListener l) {
        view.setOnClickListener(l);
        return this;
    }

    public ViewHelperImpl setOnLongClickListener(View.OnLongClickListener l) {
        view.setOnLongClickListener(l);
        return this;
    }

    public ViewHelperImpl setOnTouchListener(View.OnTouchListener l) {
        view.setOnTouchListener(l);
        return this;
    }

    public ViewHelperImpl setAdapter(Adapter adapter) {
        ((AdapterView) view).setAdapter(adapter);
        return this;
    }

    public ViewHelperImpl setRecyclerAdapter(RecyclerView.Adapter adapter) {
        ((RecyclerView) view).setAdapter(adapter);
        return this;
    }

    public ViewHelperImpl setEnable(boolean enable) {
        view.setEnabled(enable);
        return this;
    }

    public ViewHelperImpl setTextSizeDp(float size) {
        ((TextView) view).setTextSize(size);
        return this;
    }

    public ViewHelperImpl setTextSize(float size) {
        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        return this;
    }
}
