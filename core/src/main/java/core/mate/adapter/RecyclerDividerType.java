package core.mate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 封装了分隔符处理的Operator
 *
 * @author DrkCore
 * @since 2016年1月18日21:01:24
 */
public class RecyclerDividerType extends AbsRecyclerItemType<Divider> {

	/* 继承 */

    @Override
    public SimpleRecyclerViewHolder createViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new SimpleRecyclerViewHolder(new View(parent.getContext()));
    }

    @Override
    public void bindViewData(SimpleRecyclerViewHolder holder, int position, Divider data) {
        holder.setHolderSize(ViewGroup.LayoutParams.MATCH_PARENT, data.getHeight());
        holder.setHolderBackgroundDrawable(data.getDrawable());
    }
}
