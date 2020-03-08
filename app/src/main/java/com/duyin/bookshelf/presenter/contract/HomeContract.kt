package com.duyin.bookshelf.presenter.contract

import com.duyin.bookshelf.bean.BookShelfBean
import com.edison.mvplib.impl.IPresenter
import com.edison.mvplib.impl.IView

/**
created by edison 2020-02-28
 */
interface HomeContract {

    interface Presenter : IPresenter{
        fun initBookShelfData()

    }

    interface View : IView{
        fun initBookShelfGrid(datas: List<BookShelfBean>)

        fun updateBookShelf(bookShelf: BookShelfBean)
    }


}