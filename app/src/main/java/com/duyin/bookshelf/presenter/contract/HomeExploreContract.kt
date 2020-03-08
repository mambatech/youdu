package com.duyin.bookshelf.presenter.contract

import com.duyin.bookshelf.bean.HomeExploreItemBean
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