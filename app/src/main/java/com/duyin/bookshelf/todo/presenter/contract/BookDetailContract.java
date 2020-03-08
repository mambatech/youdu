package com.duyin.bookshelf.todo.presenter.contract;

import android.content.Intent;

import com.edison.mvplib.impl.IPresenter;
import com.edison.mvplib.impl.IView;
import com.duyin.bookshelf.bean.BookChapterBean;
import com.duyin.bookshelf.bean.BookShelfBean;
import com.duyin.bookshelf.bean.SearchBookBean;

import java.util.List;

public interface BookDetailContract {
    interface Presenter extends IPresenter {
        void initData(Intent intent);

        int getOpenFrom();

        SearchBookBean getSearchBook();

        BookShelfBean getBookShelf();

        List<BookChapterBean> getChapterList();

        Boolean getInBookShelf();

        void initBookFormSearch(SearchBookBean searchBookBean);

        void getBookShelfInfo();

        void addToBookShelf();

        void removeFromBookShelf();

        void changeBookSource(SearchBookBean searchBookBean);
    }

    interface View extends IView {
        /**
         * 更新书籍详情UI
         */
        void updateView();

        /**
         * 数据获取失败
         */
        void getBookShelfError();

        void finish();

        void toast(String msg);
    }
}
