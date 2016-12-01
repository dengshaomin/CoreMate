package core.mate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.reflect.Type;
import java.util.Collection;

import core.mate.content.TextBuilder;
import core.mate.util.ClassUtil;

/**
 * 用于创建ViewType指定的布局以及绑定对应的数据的接口，设计上而言，一种ViewType应当对应着一种Operator。
 * <b>注意，当你实现该类的子类时请保留第一个泛型参数为该ViewOperator能够处理的数据类型。</b>
 * <p>比如你创建了一个类：<br/>
 * <b>class StringOperator extends {@link SimpleRecyclerType}< String> </b><br/>
 * 这是可以的，它将用于适配String类型的数据。但是如果此时你再创建一个类：<br/>
 * <b>class MySimpleStringOperator extends StringOperator </b><br/>
 * 这将是非法的，因为运行时无法通过泛型获知其可处理的数据类型，这将导致异常。
 * <p/>
 *
 * @param <Item>
 */
public abstract class AbsRecyclerItemType<Item, Holder extends RecyclerView.ViewHolder> {

    private Type type;

    public final boolean canHandleObject(Object obj) {
        if (type == null) {
            Type types[] = ClassUtil.getGenericParametersType(getClass());
            type = types[0];//第一个泛型参数是数据

        }
        if (obj instanceof Collection) {
            /*
             * 出于安全性和可维护性考虑，这里禁止直接适配Collection。
				* 比如obj指向一个ArrayList<String>，
				* 此时因为其并没有创建一个class对象我们无法通过{@link ClassUtil#getGenericParametersType}
				* 来获取ArrayList其中泛型的类型。
				* 同时，因为FlexibleAdapter的适配泛型是Object，
				* 会和add等方法冲突导致找不到可适配的ViewOperator。
				*
				* 如果必须要使用Collection的话，可以使用数组类型，或者使用一个JavaBean来包裹所需的数据。
				*/
            TextBuilder textBuilder = new TextBuilder();
            textBuilder.append("obj = ", obj).appendNewLine("  type = ", obj.getClass())
                    .appendNewLine("支持泛型 = ", type).appendNewLine("ViewOperator不允许直接适配Collection或者其子类");

            throw new IllegalStateException(textBuilder.toString());
        }
        return type == obj.getClass() && canHandleItem((Item) obj);
    }

    /**
     * 判断item是否可用，默认返回true。
     *
     * @param item
     * @return
     */
    protected boolean canHandleItem(Item item) {
        return true;
    }

    public abstract Holder createViewHolder(LayoutInflater inflater, ViewGroup parent);

    public abstract void bindViewData(Holder holder, int position, Item data);

}
