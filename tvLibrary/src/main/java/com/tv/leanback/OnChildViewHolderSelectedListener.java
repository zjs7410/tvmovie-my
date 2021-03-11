package com.tv.leanback;


import androidx.recyclerview.widget.RecyclerView;

/**
 * Interface for receiving notification when a child of this ViewGroup has been selected.
 * There are two methods:
 * <li>
 *     {link {@link #onChildViewHolderSelected(RecyclerView, RecyclerView.ViewHolder, int, int)}}
 *     is called when the view holder is about to be selected.  The listener could change size
 *     of the view holder in this callback.
 * </li>
 * <li>
 *     {link {@link #onChildViewHolderSelectedAndPositioned(RecyclerView, RecyclerView.ViewHolder,
 *     int, int)} is called when view holder has been selected and laid out in RecyclerView.
 *
 * </li>
 */
public abstract class OnChildViewHolderSelectedListener {
    /**
     * Callback method to be invoked when a child of this ViewGroup has been selected. Listener
     * might change the size of the child and the position of the child is not finalized. To get
     * the final layout position of child, overide {@link #onChildViewHolderSelectedAndPositioned(
     * RecyclerView, RecyclerView.ViewHolder, int, int)}.
     *
     * @param parent The RecyclerView where the selection happened.
     * @param viewHolder The ViewHolder within the RecyclerView that is selected, or null if no
     *        view is selected.
     * @param position The position of the view in the adapter, or NO_POSITION
     *        if no view is selected.
     */
    public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder viewHolder,
                                          int position, int subposition) {
    }

    /**
     * Callback method to be invoked when a child of this ViewGroup has been selected and
     * positioned.
     *
     * @param parent The RecyclerView where the selection happened.
     * @param viewHolder The ViewHolder within the RecyclerView that is selected, or null if no
     *        view is selected.
     * @param position The position of the view in the adapter, or NO_POSITION
     *        if no view is selected.
     */
    public void onChildViewHolderSelectedAndPositioned(RecyclerView parent,
            RecyclerView.ViewHolder viewHolder, int position, int subposition) {
    }

}
