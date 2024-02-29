package com.app.ecarepro.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.app.ecarepro.R
import com.app.ecarepro.databinding.ItemOnboardingBinding
import com.app.ecarepro.model.Slide
import com.app.ecarepro.utils.imageUrl
import com.app.ecarepro.utils.loadSvgFromApi
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class OnBoardingViewPagerAdapter @Inject constructor() :
    ListAdapter<Slide, OnBoardingViewPagerAdapter.OnboardingItemViewHolder>(
        SlideItemDiffCallback()
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
        fun bind(item: Slide) {
            with(binding) {
                image.setImageResource(R.drawable.img_onboarding_1)
                title.text = item.heading
                subTitle.text = item.text
            }
        }
    }
}

class SlideItemDiffCallback : DiffUtil.ItemCallback<Slide>() {
    override fun areItemsTheSame(oldItem: Slide, newItem: Slide): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Slide, newItem: Slide): Boolean =
        oldItem == newItem

}