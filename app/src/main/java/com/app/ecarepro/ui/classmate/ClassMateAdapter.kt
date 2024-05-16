package com.app.ecarepro.ui.classmate

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.ClassMateItemBinding
import com.app.ecarepro.databinding.TeacherItemBinding
import com.app.ecarepro.model.ClassmateLST
import com.app.ecarepro.ui.common.IItemListener
import com.app.ecarepro.utils.getIcNoProfileBig
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ClassMateAdapter(
    private var studentMutableList: MutableList<ClassmateLST>,
    var callBack: IItemListener<ClassmateLST?>
) :
    RecyclerView.Adapter<ClassMateAdapter.ClassMateAdapterHolder>() {
    private var lastIndex = -1
    var isRpt = false
    var isForClassTeacher = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassMateAdapterHolder {
        val mBinding =
            ClassMateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassMateAdapterHolder(mBinding)
    }

    override fun getItemCount(): Int = studentMutableList.size

    override fun onBindViewHolder(holder: ClassMateAdapterHolder, position: Int) {
        val mBinding = DataBindingUtil.getBinding<ClassMateItemBinding>(holder.itemView)
        val nextSessionClass = studentMutableList[position]
        with(mBinding!!) {

            nextSessionClass.let {
                name.text = it.name


                getIcNoProfileBig(image.context)?.let { it1 ->
                    Picasso.get()
                        .load(it.photo)
                        .placeholder(it1)
                        .into(image)
                }

                name.setText(it.name)



                image2.setBackgroundColor(image2.context.resources.getColor(R.color.brand_color))
                val bgShape = discussRel.background as GradientDrawable
                bgShape.setColor(image2.context.resources.getColor(R.color.brand_color))


            }


        }

    }

    class ClassMateAdapterHolder(itemView: ClassMateItemBinding) :
        RecyclerView.ViewHolder(itemView.root)


}