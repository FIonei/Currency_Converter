package com.example.currencyconverter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class RecyclerViewAdapter internal constructor(context: Context?, _items: ArrayList<Valute>, var current: String) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val items: ArrayList<Valute>
    private val context: Context
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = mInflater.inflate(
            R.layout.recycler_item,
            parent,
            false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        if (item.CharCode.toString() == current) holder.check.visibility = VISIBLE
        else holder.check.visibility = GONE
    }

    private fun findIdByCode(): Int {
        return items.indexOf(ValCurs().getByCharCode(current))
    }

    override fun getItemCount(): Int = items.size
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var valuteName: TextView
        var valuteCodeName: TextView
        var check: ImageView
        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            valuteName = itemView.findViewById(R.id.name)
            valuteCodeName = itemView.findViewById(R.id.code_name)
            check = itemView.findViewById(R.id.check)
            itemView.setOnClickListener(this)
        }

        fun bind(item: Valute) {
            valuteName.text = item.Name
            valuteCodeName.text = item.CharCode
        }
    }

    fun getItem(id: Int): Valute {
        return items[id]
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    init {
        this.context = context!!
        mInflater = LayoutInflater.from(context)
        items = _items
    }
}