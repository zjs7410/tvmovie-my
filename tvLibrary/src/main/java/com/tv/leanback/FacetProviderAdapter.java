package com.tv.leanback;


import androidx.recyclerview.widget.RecyclerView;

/**
 * Optional interface that implemented by {@link RecyclerView.Adapter} to
 * query {@link FacetProvider} for a given type within Adapter.  Note that
 * {@link RecyclerView.ViewHolder} may also implement {@link FacetProvider} which
 * has a higher priority than the one returned from the FacetProviderAdapter.
 */
public interface FacetProviderAdapter {

    /**
     * Queries {@link FacetProvider} for a given type within Adapter.
     * @param type        type of the item.
     * @return Facet provider for the type.
     */
    public FacetProvider getFacetProvider(int type);

}
