package com.duyin.bookshelf.todo.bean

import androidx.annotation.IntDef
import com.duyin.bookshelf.todo.bean.HomeExploreType.Companion.BOOK_SHELF_BOOK
import com.duyin.bookshelf.todo.bean.HomeExploreType.Companion.BOOK_SHELF_TITLE
import com.duyin.bookshelf.todo.bean.HomeExploreType.Companion.EXPLORE_BEAN
import com.duyin.bookshelf.todo.bean.HomeExploreType.Companion.EXPLORE_END
import com.duyin.bookshelf.todo.bean.HomeExploreType.Companion.EXPLORE_TITLE

/**
created by edison 2020-03-05
 */
data class HomeExploreItemBean(
        @HomeExploreType val type: Int ,
        val data: Any?
) {
}

@IntDef(BOOK_SHELF_TITLE, BOOK_SHELF_BOOK, EXPLORE_TITLE, EXPLORE_BEAN,EXPLORE_END)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class HomeExploreType {
    companion object {
        //探索书架名
        const val BOOK_SHELF_TITLE = 0
        //探索书架上的书
        const val BOOK_SHELF_BOOK = 1
        //探索书源的名称
        const val EXPLORE_TITLE = 2
        //探索书源的具体类别
        const val EXPLORE_BEAN = 3

        //底部间距
        const val EXPLORE_END = 4
    }
}
