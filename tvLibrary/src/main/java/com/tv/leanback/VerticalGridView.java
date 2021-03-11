package com.tv.leanback;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;

import com.tv.R;


/**
 * A {@link android.view.ViewGroup} that shows items in a vertically scrolling list. The items
 * come from the {@link Adapter} associated with this view.
 * <p>
 * {@link Adapter} can optionally implement {@link FacetProviderAdapter} which
 * provides {@link FacetProvider} for a given view type;  {@link ViewHolder}
 * can also implement {@link FacetProvider}.  Facet from ViewHolder
 * has a higher priority than the one from FacetProviderAdapter associated with viewType.
 * Supported optional facets are:
 * <ol>
 * <li> {@link ItemAlignmentFacet}
 * When this facet is provided by ViewHolder or FacetProviderAdapter,  it will
 * override the item alignment settings set on VerticalGridView.  This facet also allows multiple
 * alignment positions within one ViewHolder.
 * </li>
 * </ol>
 */
public class VerticalGridView extends BaseGridView {
    private OnItemListener<VerticalGridView> onItemListener;

    public VerticalGridView(Context context) {
        this(context, null);
    }

    public VerticalGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLayoutManager.setOrientation(VERTICAL);
        initAttributes(context, attrs);
    }

    protected void initAttributes(Context context, AttributeSet attrs) {
        initBaseGridViewAttributes(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.lbVerticalGridView);
        setColumnWidth(a);
        setNumColumns(a.getInt(R.styleable.lbVerticalGridView_numberOfColumns, 1));
        a.recycle();
    }

    void setColumnWidth(TypedArray array) {
        TypedValue typedValue = array.peekValue(R.styleable.lbVerticalGridView_columnWidth);
        if (typedValue != null) {
            int size = array.getLayoutDimension(R.styleable.lbVerticalGridView_columnWidth, 0);
            setColumnWidth(size);
        }
    }

    /**
     * Sets the number of columns.  Defaults to one.
     */
    public void setNumColumns(int numColumns) {
        mLayoutManager.setNumRows(numColumns);
        requestLayout();
    }

    public void setFocusPosition(int position) {
        this.mLayoutManager.setFocusPosition(position);
    }

    /**
     * Sets the column width.
     *
     * @param width May be {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}, or a size
     *              in pixels. If zero, column width will be fixed based on number of columns
     *              and view width.
     */
    public void setColumnWidth(int width) {
        mLayoutManager.setRowHeight(width);
        requestLayout();
    }

    @Override
    public void onFocusChange(View itemView, boolean hasFocus) {
        if (null != itemView && itemView != this) {
            final int position = getChildAdapterPosition(itemView);
            itemView.setSelected(hasFocus);
            if (hasFocus) {
                if (null != onItemListener) {
                    onItemListener.onItemSelected(this, itemView, position);
                }
            } else {
                if (null != onItemListener) {
                    onItemListener.onItemPreSelected(this, itemView, position);
                }
            }
        }
    }

    public void setOnItemListener(OnItemListener<VerticalGridView> onItemListener) {
        this.onItemListener = onItemListener;
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        if (child.isFocusable() && null == child.getOnFocusChangeListener()) {
            child.setOnFocusChangeListener(this);
        }
    }

    long mOldTime = 0;
    long mTimeStamp = 100;

    /**
     * 设置按键滚动的时间间隔.
     * 在小于time的间隔内消除掉.
     */
    public void setKeyScrollTime(long time) {
        this.mTimeStamp = time;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        /**
         *  用于优化按键快速滚动卡顿的问题.
         */
        if (event.getRepeatCount() >= 2 && event.getAction() == KeyEvent.ACTION_DOWN) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - mOldTime <= mTimeStamp) {
                return true;
            }
            mOldTime = currentTime;
        }
        return super.dispatchKeyEvent(event);
    }
}
