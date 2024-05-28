package com.app.ecarepro.ui.circuler

 import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentCirculerDetailsBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CircularDetailsFragment : Fragment() {

    private lateinit var binding : FragmentCirculerDetailsBinding
     private lateinit var fileSource: String

    private val circularDetailsViewModel:CircularDetailsViewModel by    viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding= FragmentCirculerDetailsBinding.inflate(inflater,container,false)

        val circularID=  requireArguments().getInt(Constant.CIRCULAR_ID)
        circularDetailsViewModel.getCircularDTL(circularID,Constant.DEFAULT_ID_CIRCULAR)

         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.relView.setOnClickListener {
            findNavController().navigate(R.id.action_circularDetailsFragment_to_openPdfFragment,Bundle( ).apply {
                putString(Constant.URL_ARGUMENT, fileSource)
            })
        }

        binding.relDownload.setOnClickListener {
            val androidDownloader = AndroidDownloader(requireContext())
            androidDownloader.downloadFile(fileSource, getString(R.string.circular))
        }

        lifecycleScope.launch {
            circularDetailsViewModel._circularDTLStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){
                            binding.circularDetails=it.data.circuler
                            fileSource=it.data.circuler.filePath

                        }
                    }


                }
            }
        }
    }
}