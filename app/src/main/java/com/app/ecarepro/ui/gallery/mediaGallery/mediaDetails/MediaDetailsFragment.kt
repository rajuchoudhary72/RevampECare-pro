package com.app.ecarepro.ui.gallery.mediaGallery.mediaDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentMediaDetailsBinding
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.imageUrl
import com.squareup.picasso.Picasso


class MediaDetailsFragment : Fragment() {

    private lateinit var binding : FragmentMediaDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentMediaDetailsBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            arguments?.getString(headline)?.let { headlineLoc ->
                name.text=headlineLoc
            }
            arguments?.getString(newsName)?.let { newsNameLoc ->
                tvNewspaper.text=newsNameLoc
            }
            arguments?.getString(publishedOn)?.let { publishedOnLoc ->
                tvPubliOn.text=publishedOnLoc
            }
            arguments?.getString(updatedOn)?.let { updatedOnLoc ->
                tvUpdtedOn.text=updatedOnLoc
            }
            arguments?.getString(description)?.let { descriptionLoc ->
                tvDes.text=descriptionLoc
            }
            arguments?.getString(fileName)?.let { photo ->
                Picasso.get().load(photo)
                    //.placeholder(R.drawable.default_profile)
                    .into(ivPhoto)
            }




        }


    }


    companion object {
        const val description = ""
        const val fileName = ""
        const val fileNameFullSize = ""
        const val headline = ""
        const val id = ""
        const val newsName = ""
        const val publishedOn = ""
        const val updatedOn = ""
    }

}