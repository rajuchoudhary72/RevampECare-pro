package com.app.ecarepro.ui.discipline_log.infraction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentInfrectionSelectBinding
import com.app.ecarepro.utils.Constant


class InfractionSelectFragment : Fragment() {

    private lateinit var binding : FragmentInfrectionSelectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
       binding=FragmentInfrectionSelectBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAddInfraction.setOnClickListener {
            findNavController().navigate(R.id.action_infractionSelectFragment_to_studentListFragment2,Bundle( ).apply {
                putString(Constant.TO,  getString(R.string.add_infraction))
            })
        }
        binding.tvViewInfraction.setOnClickListener {
            findNavController().navigate(R.id.action_infractionSelectFragment_to_studentListFragment2,Bundle( ).apply {
                putString(Constant.TO,  getString(R.string.view_infraction))
            })
        }

    }
}