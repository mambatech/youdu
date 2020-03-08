package com.duyin.bookshelf.todo.presenter.contract

import android.service.autofill.Dataset
import com.duyin.bookshelf.todo.bean.BookShelfBean
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