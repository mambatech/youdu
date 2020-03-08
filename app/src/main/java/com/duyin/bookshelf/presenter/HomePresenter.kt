package com.duyin.bookshelf.presenter

import com.duyin.bookshelf.MApplication
import com.duyin.bookshelf.base.observer.MyObserver
import com.duyin.bookshelf.bean.BookShelfBean
import com.duyin.bookshelf.todo.constant.RxBusTag
import com.duyin.bookshelf.todo.help.BookshelfHelp
import com.duyin.bookshelf.presenter.contract.HomeContract
import com.duyin.bookshelf.utils.GsonUtils
import com.duyin.bookshelf.utils.ReadAssets
import com.duyin.bookshelf.utils.RxUtils
import com.duyin.bookshelf.utils.SharePreferenceHelper
import com.edison.mvplib.BasePresenterImpl
import com.edison.mvplib.impl.IView
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import io.reactivex.Observable

/**
created by edison 2020-02-28
 */
class HomePresenter : BasePresenterImpl<HomeContract.View>(), HomeContract.Presenter {

    companion object{
        val KEY_HOME_BOOK_SHELF = "home_book_shelf"
    }

    override fun initBookShelfData() {
        val localBookShelf = BookshelfHelp.getAllBookWithoutShlefType()
        if (localBookShelf.size <= 0) {
            loadAssestBookShelf().compose(RxUtils::toSimpleSingle)
                    .subscribe(object : MyObserver<Boolean>() {
                        override fun onNext(t: Boolean) {
                            if (mView != null) {
                                mView.initBookShelfGrid(BookshelfHelp.getAllBookWithoutShlefType())
                            }
                        }

                    })
        } else {
            if (mView != null){
                mView.initBookShelfGrid(localBookShelf)
            }
        }
    }

    /**
     * 具体的书籍
     */
    private fun loadAssestBookShelf(): Observable<Boolean> {
        return Observable.create{ emmiter ->
            val hasLoad = SharePreferenceHelper.getBoolean(KEY_HOME_BOOK_SHELF,false)
            if (!hasLoad) {
                val str = ReadAssets.getText(MApplication.getInstance(), "local_bookshelf_list.json")
                val bookShelfList: List<BookShelfBean> = GsonUtils.parseJArray(str, BookShelfBean::class.java)
                BookshelfHelp.saveBooksToShelf(bookShelfList)
                SharePreferenceHelper.setBoolean(KEY_HOME_BOOK_SHELF,true)
            }

            emmiter.onNext(true)
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(RxBusTag.HAD_ADD_BOOK)])
    fun hadAddOrRemoveBook(bookShelfBean: BookShelfBean) {
        mView.updateBookShelf(bookShelfBean)
    }

    override fun attachView(iView: IView) {
        super.attachView(iView)
        RxBus.get().register(this)
    }

    override fun detachView() {
        RxBus.get().unregister(this)
    }
}