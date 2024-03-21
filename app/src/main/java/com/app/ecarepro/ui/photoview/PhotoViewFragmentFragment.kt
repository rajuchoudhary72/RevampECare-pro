package com.app.ecarepro.ui.photoview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentPhotoViewBinding
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.imageUrl
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PhotoViewFragmentFragment : Fragment() {

    private var _binding: FragmentPhotoViewBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        arguments?.getString(PHOTO)?.let { photo ->
            binding.photoView.imageUrl(photo)
        }

        binding.btnDownload.setOnClickListener {
            arguments?.getString(PHOTO)?.let { photo ->
                val androidDownloader = AndroidDownloader(requireContext())
                androidDownloader.downloadFile(photo, "Photo", "image/jpeg")
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PHOTO = "photo"
    }
}