package com.pj567.movie.event;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class TopStateEvent {
    public final static int TYPE_TOP = 0;
    public int type;

    public TopStateEvent(int type) {
        this.type = type;
    }
}