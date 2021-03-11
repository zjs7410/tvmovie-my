package com.tv.leanback;


/**
 * This is the query interface to supply optional features(aka facets) on an object without the need
 * of letting the object to subclass or implement java interfaces.
 */
public interface FacetProvider {

    /**
     * Queries optional implemented facet.
     * @param facetClass  Facet classes to query,  examples are: class of
     *                    {@link ItemAlignmentFacet}.
     * @return Facet implementation for the facetClass or null if feature not implemented.
     */
    public Object getFacet(Class<?> facetClass);

}
