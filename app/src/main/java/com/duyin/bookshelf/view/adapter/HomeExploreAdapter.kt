package com.duyin.bookshelf.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duyin.bookshelf.MApplication
import com.duyin.bookshelf.R
import com.duyin.bookshelf.bean.BookShelfBean
import com.duyin.bookshelf.bean.FindKindBean
import com.duyin.bookshelf.bean.HomeExploreItemBean
import com.duyin.bookshelf.bean.HomeExploreType
import com.duyin.bookshelf.helper.ImageLoader
import com.duyin.bookshelf.utils.ScreenUtils
import com.duyin.bookshelf.view.holder.BaseVH
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

/**
created by edison 2020-03-01
 */
class HomeExploreAdapter: RecyclerView.Adapter<BaseVH> {

    var itemCallback: BookShelfItemListener? = null
    private var screenWidth: Int = 0

    constructor(){
        screenWidth = ScreenUtils.getScreenWidth(MApplication.getInstance())
    }

    interface BookShelfItemListener{
        fun onItemBookClicked(bookShelf: BookShelfBean)
        fun onItemKindBeanCliked(findKindBean: FindKindBean)
    }

    private val datas = ArrayList<HomeExploreItemBean>()

    fun updateDatas(list: List<HomeExploreItemBean>){
        if (list.isNotEmpty()) {
            datas.clear()
            datas.addAll(list)
            datas.add(HomeExploreItemBean(HomeExploreType.EXPLORE_END, null))
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        return when (viewType) {
            HomeExploreType.BOOK_SHELF_BOOK -> {
                val root = LayoutInflater.from(parent.context).inflate(R.layout.item_explore_book_shelf, parent, false)
                ExploreBookVH(root)
            }
            HomeExploreType.BOOK_SHELF_TITLE -> {
                val root = LayoutInflater.from(parent.context).inflate(R.layout.item_home_explore_title, parent, false)
                TitleVH(root)
            }
            HomeExploreType.EXPLORE_BEAN -> {
                val root = LayoutInflater.from(parent.context).inflate(R.layout.item_explore_book_source_bean, parent, false)
                ExploreSourceBeanVH(root)
            }

            HomeExploreType.EXPLORE_END -> {
                val root = LayoutInflater.from(parent.context).inflate(R.layout.item_home_explore_end, parent, false)
                ExploreEndVH(root)
            }

            else -> {
                val root = LayoutInflater.from(parent.context).inflate(R.layout.item_explore_book_title, parent, false)
                ExploreBeanTitleVH(root)
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        val itemBean = datas[position].data

        if (holder is TitleVH){
            holder.tvTitle.text = itemBean.toString()

        }else if (holder is ExploreBookVH){
            val lp = holder.itemView.layoutParams
            lp.width = screenWidth / 3
            val bookShelfBean = itemBean as BookShelfBean?
            val bookInfoBean = bookShelfBean?.bookInfoBean

            if (bookInfoBean != null) {
                holder.tvName.text = bookInfoBean.name
                ImageLoader.load(bookInfoBean.coverUrl)
                        .placeholder(R.drawable.image_cover_default)
                        .error(R.drawable.image_cover_default)
                        .transform(RoundedCornersTransformation(6, 0))
                        .into(holder.ivCover)

                holder.llItem.setOnClickListener {
                    itemCallback?.onItemBookClicked(bookShelfBean)
                }
            }
        }else if (holder is ExploreBeanTitleVH){
            holder.tvSourceTitle.text = itemBean.toString()

        }else if (holder is ExploreSourceBeanVH){
            val findKindBook = itemBean as FindKindBean
            holder.tvSourceBeanName.text = findKindBook.kindName
            holder.tvSourceBeanName.setOnClickListener {
                itemCallback?.onItemKindBeanCliked(findKindBook)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return datas[position].type
    }

    class ExploreEndVH(rootView: View): BaseVH(rootView){
        val tvEnd = rootView.findViewById<TextView>(R.id.tv_end)
    }

    class ExploreBeanTitleVH(rootView: View): BaseVH(rootView){
        val tvSourceTitle = rootView.findViewById<TextView>(R.id.tv_source_name)
    }

    class ExploreSourceBeanVH(rootView: View): BaseVH(rootView){
        val tvSourceBeanName = rootView.findViewById<TextView>(R.id.tv_item)
    }

    class ExploreBookVH(rootView: View): BaseVH(rootView){
        val llItem = rootView.findViewById<LinearLayout>(R.id.ll_item)
        val tvName = rootView.findViewById<TextView>(R.id.tv_name)
        val ivCover = rootView.findViewById<ImageView>(R.id.iv_cover)
    }

    class TitleVH(rootView: View): BaseVH(rootView){
        val tvTitle = rootView.findViewById<TextView>(R.id.tv_title)
    }
}