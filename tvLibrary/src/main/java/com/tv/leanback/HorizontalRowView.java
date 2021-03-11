package com.tv.leanback;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tv.R;


/**
 * ListRowView is a {@link ViewGroup} which always contains a
 * {@link HorizontalGridView}, and may optionally include a hover card.
 */
public final class HorizontalRowView extends LinearLayout {

    private HorizontalGridView mGridView;

    public HorizontalRowView(Context context) {
        this(context, null);
    }

    public HorizontalRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.lb_list_row, this);

        mGridView = (HorizontalGridView) findViewById(R.id.row_content);
        // since we use WRAP_CONTENT for height in lb_list_row, we need set fixed size to false
        mGridView.setHasFixedSize(false);

        // Uncomment this to experiment with page-based scrolling.
        // mGridView.setFocusScrollStrategy(HorizontalGridView.FOCUS_SCROLL_PAGE);

        setOrientation(LinearLayout.VERTICAL);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
    }

    /**
     * Returns the HorizontalGridView.
     */
    public HorizontalGridView getGridView() {
        return mGridView;
    }

}
