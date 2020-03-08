package com.duyin.bookshelf.todo.presenter

import android.text.TextUtils
import android.util.Log
import com.duyin.bookshelf.MApplication
import com.duyin.bookshelf.base.observer.MyObserver
import com.duyin.bookshelf.todo.bean.*
import com.duyin.bookshelf.todo.constant.AppConstant.SCRIPT_ENGINE
import com.duyin.bookshelf.todo.help.BookshelfHelp
import com.duyin.bookshelf.todo.model.BookSourceManager
import com.duyin.bookshelf.todo.model.analyzeRule.AnalyzeRule
import com.duyin.bookshelf.todo.presenter.contract.HomeExploreContract
import com.duyin.bookshelf.todo.widget.recycler.expandable.bean.RecyclerViewData
import com.duyin.bookshelf.utils.*
import com.edison.mvplib.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.ArrayList
import javax.script.SimpleBindings

/**
created by edison 2020-02-29
 */
class HomeExplorePresenter : BasePresenterImpl<HomeExploreContract.View>(),HomeExploreContract.Presenter {

    private var analyzeRule: AnalyzeRule? = null
    private val findError = "发现规则语法错误"

    override fun initExploreData() {
        loadBookSourceObservable()
                .map { t -> passBookSource(t) }
                .map { t ->
                    val bookShelfList = loadAssestExploreBook()
                    val ret = ArrayList<HomeExploreItemBean>()
                    ret.addAll(bookShelfList)
                    ret.addAll(t)
                    return@map ret
                }
                .compose(RxUtils::toSimpleSingle)
                .subscribe(object : MyObserver<List<HomeExploreItemBean>>() {
                    override fun onNext(t: List<HomeExploreItemBean>) {
                        if (mView != null) {
                            mView.initExploreBookShelf(t)
                        }
                    }
                })
    }

    override fun detachView() {

    }

    /**
     * 书源
     */
    private fun loadBookSourceObservable(): Observable<List<BookSourceBean>> {
        return Observable.create { emitter ->
            val allSource = BookSourceManager.getAllBookSourceBySerialNumber()
            if (allSource.isEmpty()) {
                val str = ReadAssets.getText(MApplication.getInstance(), "localbook_data.json")
                BookSourceManager.importBookSourceFromJson(str)

                emitter.onNext(BookSourceManager.getAllBookSourceBySerialNumber())
            }else{
                emitter.onNext(allSource)
            }
        }
    }

    /**
     * 每个书源下的所有分类
     */
    private fun passBookSource(sourceBeans: List<BookSourceBean>): List<HomeExploreItemBean> {
        val aCache = ACache.get(mView.context, "findCache")
        val exploreList = ArrayList<HomeExploreItemBean>()
        for (sourceBean in sourceBeans) {
            try {
                val kindA: Array<String>
                var findRule: String?
                if (!TextUtils.isEmpty(sourceBean.ruleFindUrl) && !sourceBean.containsGroup(findError)) {
                    var isJsAndCache = sourceBean.ruleFindUrl.startsWith("<js>")
                    if (isJsAndCache) {
                        findRule = aCache.getAsString(sourceBean.bookSourceUrl)
                        if (TextUtils.isEmpty(findRule)) {
                            val jsStr = sourceBean.ruleFindUrl.substring(4, sourceBean.ruleFindUrl.lastIndexOf("<"))
                            findRule = evalJS(jsStr, sourceBean.bookSourceUrl).toString()
                        } else {
                            isJsAndCache = false
                        }
                    } else {
                        findRule = sourceBean.ruleFindUrl
                    }
                    kindA = findRule!!.split("(&&|\n)+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val children = ArrayList<HomeExploreItemBean>()
                    for (kindB in kindA) {
                        if (kindB.trim { it <= ' ' }.isEmpty()) continue
                        val kind = kindB.split("::".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val findKindBean = FindKindBean()
                        findKindBean.group = sourceBean.bookSourceName
                        findKindBean.tag = sourceBean.bookSourceUrl
                        findKindBean.kindName = kind[0]
                        findKindBean.kindUrl = kind[1]
                        children.add(HomeExploreItemBean(HomeExploreType.EXPLORE_BEAN, findKindBean))
                    }

                    if (children.isNotEmpty()) {
                        exploreList.addAll(children)
                        val position = exploreList.size - children.size
                        exploreList.add(position, HomeExploreItemBean(HomeExploreType.EXPLORE_TITLE, sourceBean.bookSourceName))
                    }

                    if (isJsAndCache) {
                        aCache.put(sourceBean.bookSourceUrl, findRule)
                    }
                }
            } catch (exception: Exception) {
                sourceBean.addGroup(findError)
                BookSourceManager.addBookSource(sourceBean)
            }
        }

        return exploreList
    }

    /**
     * 执行JS
     */
    @Throws(Exception::class)
    private fun evalJS(jsStr: String, baseUrl: String): Any {
        val bindings = SimpleBindings()
        bindings["java"] = getAnalyzeRule()
        bindings["baseUrl"] = baseUrl
        return SCRIPT_ENGINE.eval(jsStr, bindings)
    }

    private fun getAnalyzeRule(): AnalyzeRule {
        if (analyzeRule == null) {
            analyzeRule = AnalyzeRule(null)
        }
        return analyzeRule!!
    }

    /**
     * 探索页书籍
     */
    private fun loadAssestExploreBook(): List<HomeExploreItemBean> {
        val str = ReadAssets.getText(MApplication.getInstance(), "local_explore_data.json")
        val bookShelfList: List<BookShelfBean> = GsonUtils.parseJArray(str, BookShelfBean::class.java)
        val retList = ArrayList<HomeExploreItemBean>()
        for (bean in bookShelfList) {
            if (!TextUtils.isEmpty(bean.shelfTitle)) {
                val itemBean = HomeExploreItemBean(HomeExploreType.BOOK_SHELF_TITLE, bean.shelfTitle)
                retList.add(itemBean)
            } else {
                val itemBean = HomeExploreItemBean(HomeExploreType.BOOK_SHELF_BOOK, bean)
                retList.add(itemBean)
            }
        }
        return retList
    }

}