package com.tv.leanback;

import android.view.View;
import android.view.ViewGroup;

/**
 * Interface for receiving notification when a child of this
 * ViewGroup has been selected.
 * @deprecated Use {@link OnChildViewHolderSelectedListener}
 */
@Deprecated
public interface OnChildSelectedListener {
    /**
     * Callback method to be invoked when a child of this ViewGroup has been
     * selected.
     *
     * @param parent The ViewGroup where the selection happened.
     * @param view The view within the ViewGroup that is selected, or null if no
     *        view is selected.
     * @param position The position of the view in the adapter, or NO_POSITION
     *        if no view is selected.
     * @param id The id of the child that is selected, or NO_ID if no view is
     *        selected.
     */
    void onChildSelected(ViewGroup parent, View view, int position, long id);
}
