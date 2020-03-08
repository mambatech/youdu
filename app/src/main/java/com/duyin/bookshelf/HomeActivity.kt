package com.duyin.bookshelf

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.duyin.bookshelf.base.MBaseActivity
import com.duyin.bookshelf.todo.bean.BookTabEntity
import com.duyin.bookshelf.todo.presenter.HomePresenter
import com.duyin.bookshelf.todo.presenter.MainPresenter
import com.duyin.bookshelf.todo.presenter.contract.HomeContract
import com.duyin.bookshelf.todo.presenter.contract.MainContract
import com.duyin.bookshelf.todo.view.fragment.*
import com.duyin.bookshelf.utils.bar.ImmersionBar
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import kotlin.collections.ArrayList

/**
created by edison 2020-02-28
 */
class HomeActivity : MBaseActivity<MainContract.Presenter>(), MainContract.View{

    private var bottomTabs: CommonTabLayout? = null

    override fun initInjector(): MainContract.Presenter {
        return MainPresenter()
    }

    override fun onCreateActivity() {
        setContentView(R.layout.activity_home)
        bottomTabs = findViewById(R.id.tabs)
    }

    override fun initData() {
        initTabs()
    }

    private fun initTabs(){
        val bookShelfTab = BookTabEntity(getString(R.string.bookshelf),
                R.drawable.ic_tab_book_selected,R.drawable.ic_tab_book_unselected)

        val exploreTab = BookTabEntity(getString(R.string.explore),
                R.drawable.ic_tab_explore_selected,R.drawable.ic_tab_explore_unselected)

        val settingTab = BookTabEntity(getString(R.string.setting),
                R.drawable.ic_tab_setting_selected,R.drawable.ic_tab_setting_unselected)

        val tabList = ArrayList<CustomTabEntity>()
        tabList.add(bookShelfTab)
        tabList.add(exploreTab)
        tabList.add(settingTab)

        bottomTabs?.setTabData(tabList,this,R.id.view_pager,createTabFragments())
    }

    private fun createTabFragments(): ArrayList<Fragment> {

        val list = ArrayList<Fragment>()
        list.add(HomeBookShelfFragment())
        list.add(HomeExploreFragment())
        list.add(MySettingFragment())
        return list
    }

    override fun isImmersionBarEnabled(): Boolean {
        return true
    }

    override fun initImmersionBar() {
        ImmersionBar.with(this).statusBarColor(R.color.white).init()
        if (ImmersionBar.isSupportStatusBarDarkFont()) {
            ImmersionBar.with(this).statusBarDarkFont(true).init()
        }
    }

    override fun dismissHUD() {
    }

    override fun onRestore(msg: String?) {

    }

    override fun getGroup(): Int {
        return 0
    }

}