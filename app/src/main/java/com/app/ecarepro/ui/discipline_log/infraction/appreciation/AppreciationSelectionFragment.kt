 package com.app.ecarepro.ui.discipline_log.infraction.appreciation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.AddMoreFavouritesBindingModel_
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentAppreciationSelectionBinding
import com.app.ecarepro.utils.Constant


 class AppreciationSelectionFragment : Fragment() {

    private lateinit var binding : FragmentAppreciationSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentAppreciationSelectionBinding.inflate(inflater,container,false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         binding.tvAddAppreciation.setOnClickListener {
             findNavController().navigate(R.id.action_appreciationSelectionFragment_to_studentListFragment2,Bundle( ).apply {
                 putString(Constant.TO,  Constant.FRA_ADD_APPRE)
             })
         }
         binding.tvViewAppreciation.setOnClickListener {
             findNavController().navigate(R.id.action_appreciationSelectionFragment_to_studentListFragment2,Bundle( ).apply {
                 putString(Constant.TO, Constant.FRA_VIEW_APPRE )
             })
         }

     }
 }