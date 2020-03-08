package com.duyin.bookshelf.todo.presenter.contract

import com.duyin.bookshelf.todo.bean.HomeExploreItemBean
import com.edison.mvplib.impl.IPresenter
import com.edison.mvplib.impl.IView

/**
created by edison 2020-02-29
 */
interface HomeExploreContract {

    interface Presenter : IPresenter{

        fun initExploreData()

    }

    interface View : IView {
        fun initExploreBookShelf(datas: List<HomeExploreItemBean>)

    }

}