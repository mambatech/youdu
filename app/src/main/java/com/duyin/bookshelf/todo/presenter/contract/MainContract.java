package com.duyin.bookshelf.todo.presenter.contract;

import com.edison.mvplib.impl.IPresenter;
import com.edison.mvplib.impl.IView;

public interface MainContract {

    interface View extends IView {

        void initImmersionBar();

        /**
         * 取消弹出框
         */
        void dismissHUD();

        /**
         * 恢复数据
         */
        void onRestore(String msg);

        void recreate();

        void toast(String msg);

        void toast(int strId);

        int getGroup();
    }

    interface Presenter extends IPresenter {

        void addBookUrl(String bookUrl);
    }

}
