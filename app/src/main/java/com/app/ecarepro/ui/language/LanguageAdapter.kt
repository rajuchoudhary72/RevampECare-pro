package com.app.ecarepro.ui.language


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R

class LanguageAdapter(
    private val languages: List<String>,
    private var selectedLanguageCode: String?,
    private val onLanguageSelected: (String) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedPosition: Int = -1

    inner class LanguageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val languageName: TextView = view.findViewById(R.id.languageName)
        val radioButton: RadioButton = view.findViewById(R.id.radioButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.languageName.text = language
        holder.radioButton.isChecked = (position == selectedPosition)

        holder.itemView.setOnClickListener { updateSelection(position) }
        holder.radioButton.setOnClickListener { updateSelection(position) }
    }

    override fun getItemCount() = languages.size

    private fun updateSelection(newPosition: Int) {
        val previousPosition = selectedPosition
        selectedPosition = newPosition
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
        onLanguageSelected(languages[selectedPosition])
    }
}
