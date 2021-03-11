package com.pj567.movie.server;

/**
 * @author pj567
 * @date :2021/1/5
 * @description:
 */
public interface DataReceiver {
    /**
     * @param keyCode
     * @param keyAction : 0 = keypressed, 1 = keyDown, 2 = keyUp
     */
    void onKeyEventReceived(String keyCode, int keyAction);

    /**
     * @param text
     */
    void onTextReceived(String text);

    /**
     * @param text
     */
    void onProjectionReceived(String text);
}