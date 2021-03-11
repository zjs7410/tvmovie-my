package com.tv.leanback;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.tv.leanback.ItemAlignmentFacet.ITEM_ALIGN_OFFSET_PERCENT_DISABLED;


/**
 * Helper class to handle ItemAlignmentFacet in a grid view.
 */
class ItemAlignmentFacetHelper {

    private static Rect sRect = new Rect();

    /**
     * get alignment position relative to optical left/top of itemView.
     */
    static int getAlignmentPosition(View itemView, ItemAlignmentFacet.ItemAlignmentDef facet,
            int orientation) {
        GridLayoutManager.LayoutParams p = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
        View view = itemView;
        if (facet.mViewId != 0) {
            view = itemView.findViewById(facet.mViewId);
            if (view == null) {
                view = itemView;
            }
        }
        int alignPos = facet.mOffset;
        if (orientation == HORIZONTAL) {
            if (facet.mOffset >= 0) {
                if (facet.mOffsetWithPadding) {
                    alignPos += view.getPaddingLeft();
                }
            } else {
                if (facet.mOffsetWithPadding) {
                    alignPos -= view.getPaddingRight();
                }
            }
            if (facet.mOffsetPercent != ITEM_ALIGN_OFFSET_PERCENT_DISABLED) {
                alignPos += ((view == itemView ? p.getOpticalWidth(view) : view.getWidth())
                        * facet.mOffsetPercent) / 100f;
            }
            if (itemView != view) {
                sRect.left = alignPos;
                ((ViewGroup) itemView).offsetDescendantRectToMyCoords(view, sRect);
                alignPos = sRect.left - p.getOpticalLeftInset();
            }
        } else {
            if (facet.mOffset >= 0) {
                if (facet.mOffsetWithPadding) {
                    alignPos += view.getPaddingTop();
                }
            } else {
                if (facet.mOffsetWithPadding) {
                    alignPos -= view.getPaddingBottom();
                }
            }
            if (facet.mOffsetPercent != ITEM_ALIGN_OFFSET_PERCENT_DISABLED) {
                alignPos += ((view == itemView ? p.getOpticalHeight(view) : view.getHeight())
                        * facet.mOffsetPercent) / 100f;
            }
            if (itemView != view) {
                sRect.top = alignPos;
                ((ViewGroup) itemView).offsetDescendantRectToMyCoords(view, sRect);
                alignPos = sRect.top - p.getOpticalTopInset();
            }
            if (view instanceof TextView && facet.isAlignedToTextViewBaseLine()) {
                Paint textPaint = ((TextView)view).getPaint();
                int titleViewTextHeight = -textPaint.getFontMetricsInt().top;
                alignPos += titleViewTextHeight;
            }
        }
        return alignPos;
    }

}
