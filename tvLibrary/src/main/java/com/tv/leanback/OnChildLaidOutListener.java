package com.tv.leanback;

import android.view.View;
import android.view.ViewGroup;

/**
 * Interface for receiving notification when a child of this
 * ViewGroup has been laid out.
 */
public interface OnChildLaidOutListener {
    /**
     * Callback method to be invoked when a child of this ViewGroup has been
     * added to the view hierarchy and has been laid out.
     *
     * @param parent The ViewGroup where the layout happened.
     * @param view The view within the ViewGroup that was laid out.
     * @param position The position of the view in the adapter.
     * @param id The id of the child.
     */
    void onChildLaidOut(ViewGroup parent, View view, int position, long id);
}
