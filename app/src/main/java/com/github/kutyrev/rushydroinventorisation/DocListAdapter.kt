package com.github.kutyrev.rushydroinventorisation

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class DocListAdapter : ListAdapter<Good, GoodsHolder>(LISTENTITY_COMPARATOR) {
    companion object {
        private val LISTENTITY_COMPARATOR = object : DiffUtil.ItemCallback<Good>() {
            override fun areItemsTheSame(oldItem: Good, newItem: Good): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Good, newItem: Good): Boolean {
                return oldItem.barCode == newItem.barCode
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsHolder {
        return GoodsHolder.create(parent)
    }

    override fun onBindViewHolder(holder: GoodsHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    fun incFactNum(barcode : String) : Boolean{
        for(curGood in currentList){
            if (curGood.barCode == barcode){
                curGood.factNumber = curGood.factNumber?.plus(1)
                submitList(currentList)
                return true
            }
        }
        return false
    }

}

class GoodsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val name : TextView = itemView.findViewById(R.id.textview_good_name)
    private val barcode : TextView = itemView.findViewById(R.id.textview_good_barcode)
    private val accNumber : TextView = itemView.findViewById(R.id.textview_account_number)
    private val factNumber : EditText = itemView.findViewById(R.id.textview_fact_number)

    private var curGood: Good? = null

    init {
        factNumber.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty())curGood?.factNumber = 0
                         else curGood?.factNumber = s.toString().toInt()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //curGood?.factNumber = s.toString().toInt()
                }
            })
        }

    fun bind(good : Good){
        name.text = good.name
        barcode.text = good.barCode
        accNumber.text = good.accountNumber.toString()
        factNumber.setText(good.factNumber.toString())
        curGood = good
    }

    companion object {
        fun create(parent: ViewGroup): GoodsHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_recyclerview_item, parent, false)

            return GoodsHolder(view)

        }
    }
}