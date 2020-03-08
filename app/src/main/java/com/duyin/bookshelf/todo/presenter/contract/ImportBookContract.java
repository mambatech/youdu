package com.duyin.bookshelf.todo.presenter.contract;

import com.edison.mvplib.impl.IPresenter;
import com.edison.mvplib.impl.IView;

import java.io.File;
import java.util.List;

public interface ImportBookContract {

    interface Presenter extends IPresenter {

        void importBooks(List<File> books);

    }

    interface View extends IView {

        /**
         * 添加成功
         */
        void addSuccess();

        /**
         * 添加失败
         */
        void addError(String msg);
    }
}
