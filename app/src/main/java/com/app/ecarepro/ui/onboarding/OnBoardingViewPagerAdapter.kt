package com.app.ecarepro.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.databinding.ItemOnboardingBinding
import com.app.ecarepro.model.OnboardingItem
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class OnBoardingViewPagerAdapter @Inject constructor() :
    ListAdapter<OnboardingItem, OnBoardingViewPagerAdapter.OnboardingItemViewHolder>(
        UserItemDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {
        return OnboardingItemViewHolder(
            ItemOnboardingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OnboardingItemViewHolder(val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnboardingItem) {
            with(binding){
                image.setImageResource(item.imageRes)
                title.setText(item.title)
                subTitle.setText(item.description)
            }
        }
    }
}

class UserItemDiffCallback : DiffUtil.ItemCallback<OnboardingItem>() {
    override fun areItemsTheSame(oldItem: OnboardingItem, newItem: OnboardingItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: OnboardingItem, newItem: OnboardingItem): Boolean =
        oldItem == newItem

}