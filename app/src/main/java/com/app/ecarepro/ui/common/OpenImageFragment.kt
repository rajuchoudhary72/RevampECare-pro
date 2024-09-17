package com.app.ecarepro.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentOpenImageBinding
import com.app.ecarepro.utils.Constant
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class OpenImageFragment : Fragment() {

    private lateinit var binding : FragmentOpenImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentOpenImageBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val imageUrl=  requireArguments().getString(Constant.URL_ARGUMENT)
            binding.pbIma.isVisible=true

            binding.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            Picasso.get()
                .load(imageUrl)
                .into(binding.ivShowAttachment, object : Callback {
                    override fun onSuccess() {
                        binding.pbIma.isVisible=false
                    }
                    override fun onError(e: Exception?) {
                        binding.pbIma.isVisible=false
                    }  })
        }catch (e:Exception){ }




    }
}