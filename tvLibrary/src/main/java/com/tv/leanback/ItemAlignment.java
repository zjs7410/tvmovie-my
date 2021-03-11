package com.tv.leanback;

import android.view.View;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;


/**
 * Defines alignment position on two directions of an item view. Typically item
 * view alignment is at the center of the view. The class allows defining
 * alignment at left/right or fixed offset/percentage position; it also allows
 * using descendant view by id match.
 */
class ItemAlignment {

    final static class Axis extends ItemAlignmentFacet.ItemAlignmentDef {
        private int mOrientation;

        Axis(int orientation) {
            mOrientation = orientation;
        }

        /**
         * get alignment position relative to optical left/top of itemView.
         */
        public int getAlignmentPosition(View itemView) {
            return ItemAlignmentFacetHelper.getAlignmentPosition(itemView, this, mOrientation);
        }
    }

    private int mOrientation = HORIZONTAL;

    final public Axis vertical = new Axis(VERTICAL);

    final public Axis horizontal = new Axis(HORIZONTAL);

    private Axis mMainAxis = horizontal;

    private Axis mSecondAxis = vertical;

    final public Axis mainAxis() {
        return mMainAxis;
    }

    final public Axis secondAxis() {
        return mSecondAxis;
    }

    final public void setOrientation(int orientation) {
        mOrientation = orientation;
        if (mOrientation == HORIZONTAL) {
            mMainAxis = horizontal;
            mSecondAxis = vertical;
        } else {
            mMainAxis = vertical;
            mSecondAxis = horizontal;
        }
    }

    final public int getOrientation() {
        return mOrientation;
    }


}
