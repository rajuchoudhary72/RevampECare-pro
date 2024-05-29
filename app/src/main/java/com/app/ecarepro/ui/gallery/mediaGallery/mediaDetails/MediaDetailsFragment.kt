package com.app.ecarepro.ui.gallery.mediaGallery.mediaDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentMediaDetailsBinding
import com.app.ecarepro.ui.photoview.PhotoViewFragmentFragment
import com.app.ecarepro.utils.imageUrl


class MediaDetailsFragment : Fragment() {

    private lateinit var binding : FragmentMediaDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentMediaDetailsBinding.inflate(inflater,container,false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            arguments?.getString(fileName)?.let { photo ->
                 ivPhoto.imageUrl(photo)
            }
            arguments?.getString(headline)?.let { t ->
                name.text=t
            }
            arguments?.getString(newsName)?.let { t ->
                tvNewspaper.text=t
            }
            arguments?.getString(publishedOn)?.let { t ->
                tvPubliOn.text=t
            }
            arguments?.getString(updatedOn)?.let { t ->
                tvUpdtedOn.text=t
            }
            arguments?.getString(description)?.let { t ->
                tvDes.text=t
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