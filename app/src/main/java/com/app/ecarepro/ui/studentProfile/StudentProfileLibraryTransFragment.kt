package com.app.ecarepro.ui.studentProfile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentStudentProfileLibraryTransBinding
import com.app.ecarepro.model.Library
import com.app.ecarepro.model.LibraryTransactionX
import com.app.ecarepro.ui.studentProfile.share_data.SharedViewModelProfile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentProfileLibraryTransFragment() : Fragment() {

    private lateinit var binding: FragmentStudentProfileLibraryTransBinding
    private val sharedViewModel: SharedViewModelProfile by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentStudentProfileLibraryTransBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.getNetworkStudentProfile().observe(this.viewLifecycleOwner){
            if (it.library!=null){
               val library=it.library
                binding.libraryDaata=library

                with(binding) {
                    tvViewPendingBooks.setOnClickListener {
                        if (library.libraryTransaction!=null){
                            popUpLibraryTransaction(library.libraryTransaction)
                        }

                    }
                    tvViewFineDetails.setOnClickListener {
                        if(library.libraryFineDTL!=null){
                            popUpLibraryFine(library.libraryFineDTL)
                        }

                    }

                }
            }

        }



    }

    private fun popUpLibraryTransaction(libraryTransactionXES: List<LibraryTransactionX>) {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.popup_library_trans,null)
        val  rvDetails = view.findViewById<RecyclerView>(R.id.rvDetails)
        val  ivCross = view.findViewById<ImageView>(R.id.ivCross)
        val  tvHeading = view.findViewById<TextView>(R.id.tvHeading)
        tvHeading.text="View Library Details"

        builder.setView(view)


        val popUpListAdapterPaidFee= PopUpListAdapterLibTrans(libraryTransactionXES )
        rvDetails.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = popUpListAdapterPaidFee
        }

        ivCross.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun popUpLibraryFine(libraryFineDTL: List<LibraryTransactionX>) {

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.popup_library_trans,null)
        val  rvDetails = view.findViewById<RecyclerView>(R.id.rvDetails)
        val  ivCross = view.findViewById<ImageView>(R.id.ivCross)
        val  tvHeading = view.findViewById<TextView>(R.id.tvHeading)

        tvHeading.text="View Fine Details"

        builder.setView(view)


        val popUpListAdapterPaidFee= PopUpListAdapterLibTrans(libraryFineDTL )
        rvDetails.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = popUpListAdapterPaidFee
        }

        ivCross.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

}