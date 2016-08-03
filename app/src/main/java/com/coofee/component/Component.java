package com.coofee.component;

import com.coofee.component.bus.RxBus;

/**
 * Created by zhaocongying on 16/8/3.
 */
public class Component {

    private static final class Holder {
        public static final Component sInstance = new Component();
    }

    private final RxBus<Object> mBus = new RxBus<Object>();

    public static RxBus<Object> getBus() {
        return Holder.sInstance.mBus;
    }

}
