package com.pj567.movie.event;

/**
 * @author pj567
 * @date :2021/1/6
 * @description:
 */
public class X5Event {
    public static final int X5_SUCCESS = 0;
    public static final int X5_FAILURE = 1;
    public int type;

    public X5Event(int type) {
        this.type = type;
    }
}