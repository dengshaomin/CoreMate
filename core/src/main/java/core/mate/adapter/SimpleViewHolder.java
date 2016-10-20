package core.mate.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author DrkCore
 * @since 2016年1月18日18:24:09
 */
public class SimpleViewHolder<Item> extends AbsViewHolder<Item> {

    public SimpleViewHolder(View v) {
        super(v);
    }

	/*获取控件*/

    private SparseArray<View> viewArray;

    public final <V extends View> V getViewById(@IdRes int id) {
        if (viewArray == null) {
            viewArray = new SparseArray<>();
        }

        View view = viewArray.get(id);
        if (view == null) {
            view = getView().findViewById(id);
            if (view != null) {
                viewArray.put(id, view);
            }
        }
        return (V) view;
    }

	/*控件处理*/

    public final void setBackgroundColor(@IdRes int id, @ColorInt int color){
        View view = getViewById(id);
        view.setBackgroundColor(color);
    }

    public final void setBackgroundResource(@IdRes int id, @DrawableRes int drawableRes){
        View view = getViewById(id);
        view.setBackgroundResource(drawableRes);
    }

    public final void setBackgroundDrawable(@IdRes int id, Drawable drawable){
        View view = getViewById(id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        }else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public final void setImageDrawable(@IdRes int id, Drawable drawable) {
        ImageView imageView = getViewById(id);
        imageView.setImageDrawable(drawable);
    }

    public final void setImageBitmap(@IdRes int id, Bitmap bmp) {
        ImageView imageView = getViewById(id);
        imageView.setImageBitmap(bmp);
    }

    public final void setImageResource(@IdRes int id, @DrawableRes int drawableRes) {
        ImageView imageView = getViewById(id);
        imageView.setImageResource(drawableRes);
    }

    public final void setText(@IdRes int id, @StringRes int stringRes) {
        TextView textView = getViewById(id);
        textView.setText(stringRes);
    }

    public final void setText(@IdRes int id, CharSequence string) {
        TextView textView = getViewById(id);
        textView.setText(string);
    }

    /**
     * 设置id指定控件的点击事件，具体实现请参阅{@link #setOnClickListener(int, View.OnClickListener, boolean)}，
     * 其中autoTag默认为true
     *
     * @param id
     * @param listener
     */
    public final void setOnClickListener(@IdRes int id, View.OnClickListener listener) {
        setOnClickListener(id, listener, true);
    }

    /**
     * 设置id指定控件的点击事件。当autoTag为true时将会将本Holder设置为控件的tag
     *
     * @param id
     * @param listener
     * @param autoTag
     */
    public final void setOnClickListener(@IdRes int id, View.OnClickListener listener, boolean autoTag) {
        View view = getViewById(id);
        view.setOnClickListener(listener);
        if (autoTag) {
            view.setTag(this);
        }
    }

    public final void setVisibility(@IdRes int id, int visibility) {
        View view = getViewById(id);
        view.setVisibility(visibility);
    }

    public final void setVisible(@IdRes int id, boolean visible) {
        View view = getViewById(id);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
