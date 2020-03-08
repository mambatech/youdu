package com.duyin.bookshelf.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duyin.bookshelf.R
import com.duyin.bookshelf.base.MBaseFragment
import com.duyin.bookshelf.view.activity.ReadBookActivity
import com.duyin.bookshelf.bean.BookShelfBean
import com.duyin.bookshelf.bean.FindKindBean
import com.duyin.bookshelf.bean.HomeExploreItemBean
import com.duyin.bookshelf.bean.HomeExploreType
import com.duyin.bookshelf.presenter.HomeExplorePresenter
import com.duyin.bookshelf.presenter.ReadBookPresenter
import com.duyin.bookshelf.presenter.contract.HomeExploreContract
import com.duyin.bookshelf.todo.view.activity.ChoiceBookActivity
import com.duyin.bookshelf.todo.view.activity.SearchBookActivity
import com.duyin.bookshelf.view.adapter.HomeExploreAdapter
import com.edison.mvplib.BitIntentDataManager

/**
created by edison 2020-02-29
 */
class HomeExploreFragment: MBaseFragment<HomeExplorePresenter>(), HomeExploreContract.View {

    private var rvExplore: RecyclerView? = null
    private var tvSearch: TextView? = null

    override fun createLayoutId(): Int {
        return R.layout.fragment_home_explore
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvExplore = view.findViewById(R.id.rv_explore)
        tvSearch = view.findViewById(R.id.card_search)
        mPresenter.initExploreData()

        tvSearch?.setOnClickListener {
            val intent = Intent(activity, SearchBookActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initInjector(): HomeExplorePresenter {
        return HomeExplorePresenter()
    }

    override fun initExploreBookShelf(datas: List<HomeExploreItemBean>) {
        val adapter = HomeExploreAdapter()
        val layoutManger = GridLayoutManager(context,3)
        layoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                when(viewType){
                    HomeExploreType.BOOK_SHELF_TITLE,
                    HomeExploreType.EXPLORE_TITLE,
                    HomeExploreType.EXPLORE_END -> return 3
                }

                return 1
            }
        }

        rvExplore?.adapter = adapter
        rvExplore?.layoutManager = layoutManger
        adapter.updateDatas(datas)

        adapter.itemCallback = object : HomeExploreAdapter.BookShelfItemListener{
            override fun onItemKindBeanCliked(kindBean: FindKindBean) {
                val intent = Intent(context, ChoiceBookActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("url", kindBean.kindUrl)
                intent.putExtra("title", kindBean.kindName)
                intent.putExtra("tag", kindBean.tag)
                startActivity(intent)
            }

            override fun onItemBookClicked(bookShelf: BookShelfBean) {
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

}