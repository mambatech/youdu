package com.duyin.bookshelf.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duyin.bookshelf.R
import com.duyin.bookshelf.base.MBaseFragment
import com.duyin.bookshelf.view.activity.ReadBookActivity
import com.duyin.bookshelf.bean.BookShelfBean
import com.duyin.bookshelf.presenter.HomePresenter
import com.duyin.bookshelf.presenter.ReadBookPresenter
import com.duyin.bookshelf.presenter.contract.HomeContract
import com.duyin.bookshelf.todo.view.activity.SearchBookActivity
import com.duyin.bookshelf.view.adapter.HomeBookShelfAdapter
import com.edison.mvplib.BitIntentDataManager

/**
created by edison 2020-02-29
 */
class HomeBookShelfFragment : MBaseFragment<HomePresenter>(), HomeContract.View {

    private var rvBookShelf: RecyclerView? = null
    private var adapter: HomeBookShelfAdapter? = null

    override fun createLayoutId(): Int {
        return R.layout.fragment_home_book_shelf
    }

    override fun initInjector(): HomePresenter {
        return HomePresenter()
    }

    override fun bindView() {
        super.bindView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvBookShelf = view.findViewById(R.id.rv_books)
        mPresenter.initBookShelfData()
    }

    override fun initBookShelfGrid(datas: List<BookShelfBean>) {
        adapter = HomeBookShelfAdapter()
        rvBookShelf?.layoutManager = GridLayoutManager(context,3) as RecyclerView.LayoutManager
        rvBookShelf?.adapter = adapter
        adapter?.updateBookShelf(datas)

        adapter?.itemCallback = object : HomeBookShelfAdapter.BookShelfItemListener{
            override fun onItemAddClick() {
                val intent = Intent(activity, SearchBookActivity::class.java)
                startActivity(intent)
            }

            override fun onItemClicked(bookShelf: BookShelfBean) {
                val intent = Intent(context, ReadBookActivity::class.java)
                intent.putExtra("openFrom", ReadBookPresenter.OPEN_FROM_APP)
                val key = System.currentTimeMillis().toString()
                val bookKey = "book$key"
                intent.putExtra("bookKey", bookKey)
                BitIntentDataManager.getInstance().putData(bookKey, bookShelf.clone())
                startActivity(intent)
            }
        }
    }

    override fun updateBookShelf(bookShelf: BookShelfBean) {
        adapter?.addBookShelf(bookShelf)
    }



}