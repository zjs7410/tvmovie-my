package com.pj567.movie.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.pj567.movie.receiver.ProjectionReceiver;
import com.pj567.movie.receiver.SearchReceiver;
import com.pj567.movie.ui.activity.HomeActivity;
import com.pj567.movie.util.AppManager;

import java.io.IOException;

import static com.pj567.movie.server.RequestProcess.KEY_ACTION_DOWN;
import static com.pj567.movie.server.RequestProcess.KEY_ACTION_PRESSED;
import static com.pj567.movie.server.RequestProcess.KEY_ACTION_UP;

/**
 * @author pj567
 * @date :2021/1/4
 * @description:
 */
public class ControlManager {
    private static ControlManager instance;
    private RemoteServer mServer = null;
    public static Context mContext;

    private ControlManager() {

    }

    public static ControlManager get() {
        if (instance == null) {
            synchronized (ControlManager.class) {
                if (instance == null) {
                    instance = new ControlManager();
                }
            }
        }
        return instance;
    }

    public static void init(Context context) {
        mContext = context;
    }

    public void startServer() {
        if (mServer != null) {
            return;
        }
        do {
            mServer = new RemoteServer(RemoteServer.serverPort, mContext);
            mServer.setDataReceiver(new DataReceiver() {
                @Override
                public void onKeyEventReceived(String keyCode, final int keyAction) {
                    if (keyCode != null) {
                        final int kc = KeyEvent.keyCodeFromString(keyCode);
                        if (kc != KeyEvent.KEYCODE_UNKNOWN) {
                            switch (keyAction) {
                                case KEY_ACTION_PRESSED:
                                case KEY_ACTION_DOWN:
                                    sendKeyCode(kc);
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onTextReceived(String text) {
                    if (!TextUtils.isEmpty(text)) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", text);
                        intent.setAction(SearchReceiver.action);
                        intent.setComponent(new ComponentName(mContext, SearchReceiver.class));
                        intent.putExtras(bundle);
                        mContext.sendBroadcast(intent);
                    }
                }

                @Override
                public void onProjectionReceived(String text) {
                    if (!TextUtils.isEmpty(text)) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("html", text);
                        intent.setAction(ProjectionReceiver.action);
                        intent.setComponent(new ComponentName(mContext, ProjectionReceiver.class));
                        intent.putExtras(bundle);
                        mContext.sendBroadcast(intent);
                    }
                }
            });
            try {
                mServer.start();
                break;
            } catch (IOException ex) {
                RemoteServer.serverPort++;
                mServer.stop();
            }
        } while (RemoteServer.serverPort < 9999);
    }

    private void sendKeyCode(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //拦截HOME键
            AppManager.getInstance().backActivity(HomeActivity.class);
        } else {
            ShellUtils.execCommand("input keyevent " + keyCode, false);
        }
    }

    public void stopServer() {
        if (mServer != null && mServer.isStarting()) {
            mServer.stop();
        }
    }
}