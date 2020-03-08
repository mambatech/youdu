package com.duyin.bookshelf.todo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duyin.bookshelf.R
import com.duyin.bookshelf.todo.bean.BookShelfBean
import com.duyin.bookshelf.todo.help.ImageLoader
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

/**
created by edison 2020-02-29
 */
class HomeBookShelfAdapter: RecyclerView.Adapter<BaseVH>() {

    companion object{
        val TYPE_BOOK = 100
        val TYPE_ADD_BOOK = 101
    }

    private val bookShelfList = ArrayList<ItemWrapper>()
    var itemCallback: BookShelfItemListener? = null

    interface BookShelfItemListener{
        fun onItemClicked(bookShelf: BookShelfBean)
        fun onItemAddClick()
    }

    fun updateBookShelf(datas: List<BookShelfBean>) {
        bookShelfList.clear()
        for (book in datas){
            val wrapper = ItemWrapper(TYPE_BOOK,book)
            bookShelfList.add(wrapper)
        }

        val wrapperAdd = ItemWrapper(TYPE_ADD_BOOK,null)
        bookShelfList.add(wrapperAdd)
        notifyDataSetChanged()
    }

    fun addBookShelf(bookShelf: BookShelfBean){
        if (bookShelfList.isNotEmpty()){
            bookShelfList.add(0,ItemWrapper(TYPE_BOOK,bookShelf))
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        return if (viewType == TYPE_ADD_BOOK){
            val root = LayoutInflater.from(parent.context).inflate(R.layout.item_book_shelf_add, parent, false)
            AddBookVH(root)
        }else{
            val root = LayoutInflater.from(parent.context).inflate(R.layout.item_home_book_shelf, parent, false)
            ShelfVH(root)
        }
    }

    override fun getItemCount(): Int {
        return bookShelfList.size
    }

    override fun getItemViewType(position: Int): Int {
        return bookShelfList[position].viewType
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {

        if (holder is ShelfVH) {
            val bookShelfBean = bookShelfList[position].data as BookShelfBean?
            val bookInfoBean = bookShelfBean?.bookInfoBean
            if (bookInfoBean != null) {
                holder.tvName.text = bookInfoBean.name

                ImageLoader.load(bookInfoBean.coverUrl)
                        .placeholder(R.drawable.image_cover_default)
                        .error(R.drawable.image_cover_default)
                        .transform(RoundedCornersTransformation(4, 0))
                        .into(holder.ivCover)

                holder.llItem.setOnClickListener {
                    itemCallback?.onItemClicked(bookShelfBean)
                }
            }
        }

        if (holder is AddBookVH){
            holder.flItem.setOnClickListener {
                itemCallback?.onItemAddClick()
            }
        }
    }

    class ItemWrapper {
        var viewType: Int = 0
        var data: Any? = null

        constructor(type: Int,bean: Any?){
            viewType = type
            data = bean
        }
    }

    class AddBookVH(rootView: View): BaseVH(rootView){
        val flItem = rootView.findViewById<FrameLayout>(R.id.fl_item)
    }

    class ShelfVH(rootView: View): BaseVH(rootView){
        val llItem = rootView.findViewById<LinearLayout>(R.id.ll_item)
        val tvName = rootView.findViewById<TextView>(R.id.tv_name)
        val ivCover = rootView.findViewById<ImageView>(R.id.iv_cover)
    }



}