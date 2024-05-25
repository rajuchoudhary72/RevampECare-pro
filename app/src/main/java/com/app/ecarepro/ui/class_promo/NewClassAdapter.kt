package com.app.ecarepro.ui.class_promo


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemBinding
import com.app.ecarepro.model.NextSessionClasse
import com.app.ecarepro.ui.common.IItemListener


class NewClassAdapter(
    private var studentMutableList: MutableList<NextSessionClasse?>,
    var callBack: IItemListener<NextSessionClasse?>
) :
    RecyclerView.Adapter<NewClassAdapter.NewClassHolder>() {
    private var lastIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewClassHolder {
        val mBinding =
            ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewClassHolder(mBinding)
    }

    override fun getItemCount(): Int = studentMutableList.size

    override fun onBindViewHolder(holder: NewClassHolder, position: Int) {
        val mBinding = DataBindingUtil.getBinding<ItemBinding>(holder.itemView)
        val nextSessionClass = studentMutableList[position]
        with(mBinding!!) {
            tvClassName.text = nextSessionClass?.className
            tvClassName.setOnClickListener {
                callBack.onItemClick(nextSessionClass, position)
                lastIndex = holder.adapterPosition
                notifyDataSetChanged()
            }

            if (lastIndex == position) {
                tvClassName.setTextColor(tvClassName.context.resources.getColor(R.color.md_theme_light_primary));
            } else {
                tvClassName.setTextColor(tvClassName.context.resources.getColor(R.color.black));
            }
        }

    }

    class NewClassHolder(itemView: ItemBinding) :
        RecyclerView.ViewHolder(itemView.root)


}