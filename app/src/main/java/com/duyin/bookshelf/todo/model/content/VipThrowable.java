package com.duyin.bookshelf.todo.model.content;

import com.duyin.bookshelf.MApplication;
import com.duyin.bookshelf.R;

public class VipThrowable extends Throwable {

    private final static String tag = "VIP_THROWABLE";

    VipThrowable() {
        super(MApplication.getInstance().getString(R.string.donate_s));
    }
}
