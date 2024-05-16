package com.app.ecarepro.ui.subjectTeacher


import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.TeacherItemBinding
import com.app.ecarepro.model.AllTeacher
import com.app.ecarepro.ui.common.IItemListener
import com.app.ecarepro.utils.getIcNoProfileBig
import com.squareup.picasso.Picasso


class TeacherAdapter(
    private var studentMutableList: MutableList<AllTeacher>,
    var callBack: IItemListener<AllTeacher?>
) :
    RecyclerView.Adapter<TeacherAdapter.TeacherAdapterHolder>() {
    private var lastIndex = -1
    var isRpt = false
    var isForClassTeacher = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherAdapterHolder {
        val mBinding =
            TeacherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeacherAdapterHolder(mBinding)
    }

    override fun getItemCount(): Int = studentMutableList.size

    override fun onBindViewHolder(holder: TeacherAdapterHolder, position: Int) {
        val mBinding = DataBindingUtil.getBinding<TeacherItemBinding>(holder.itemView)
        val nextSessionClass = studentMutableList[position]
        with(mBinding!!) {

            nextSessionClass?.let {
                name.text = it.name


                getIcNoProfileBig(image.context)?.let { it1 ->
                    Picasso.get()
                        .load(it.photo)
                        .placeholder(it1)
                        .into(image)
                }

                if (isRpt) if (isForClassTeacher) subject.text =
                    "( " + it.subject + ")" else if (!TextUtils.isEmpty(
                        it.designation
                    )
                ) subject.text =
                    "( " +it.designation + ")" else subject.text =
                    "" else if (null != it.subject && !it.subject.equals("",true)
                ) subject.text =
                    "( " + it.subject+ ")" else if (!TextUtils.isEmpty(
                        it.designation
                    )
                ) subject.text =
                    "( " + it.designation+ ")" else subject.text =
                    ""

                subject.visibility = View.VISIBLE
            }


        }

    }

    class TeacherAdapterHolder(itemView: TeacherItemBinding) :
        RecyclerView.ViewHolder(itemView.root)


}