package com.app.ecarepro.ui.discipline_log.infraction.add_infraction

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAddInfractionBinding
import com.app.ecarepro.model.InfractionConsequence
import com.app.ecarepro.model.InfractionType
import com.app.ecarepro.model.Type
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.discipline_log.infraction.adapter.InfractionCatPopUpListAdapter
import com.app.ecarepro.ui.discipline_log.infraction.adapter.InfractionConsPopUpListAdapter
import com.app.ecarepro.ui.discipline_log.infraction.adapter.SubInfractionPopUpListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddInfractionFragment : Fragment() {

    private var studentID: Int = 0
    private lateinit var subInfractionCatData: Type
    private   var subInfractionSubCateList= mutableListOf<Type>()
    private lateinit var infractionConsequence: InfractionConsequence
    private var infrTypeSelected: Boolean = false
    private var SubInfrTypeSelected: Boolean = false
    private var infrConsSelected: Boolean = false
    private lateinit var infractionCatData: InfractionType
    private   var infractionConsequencesList = mutableListOf<InfractionConsequence>()
    private   var infractionTypeList= mutableListOf<InfractionType>()
    private lateinit var binding: FragmentAddInfractionBinding
    private val addInfractionViewModel : AddInfractionViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentAddInfractionBinding.inflate(inflater,container,false)
        try {
            studentID=  requireArguments().getInt(Constant.STUDENT_ID_ARGUMENT)
        } catch (_: Exception) { }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.add_infraction)


        binding.tvSelectInfractionCate.setOnClickListener {
            popUpSelectInfractionCat()
        }

        binding.tvSelectSubInfraction.setOnClickListener {
            popUpSelectSubInfractionCat()
        }

        binding.tvSelectCons.setOnClickListener {
            popUpSelectInfractionCons()
        }

        binding.tvContinue.setOnClickListener {
            saveInfraction(1)
        }
        binding.tvContinueNoti.setOnClickListener {
            saveInfraction(2)
        }






        getAddInfection()


    }

    private fun getSubInfection(infrTypeID: Int) {

        lifecycleScope.launch {
            addInfractionViewModel.subInfractionTypesStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            subInfractionSubCateList.clear()
                            if (!it.data.types.isNullOrEmpty()){

                                subInfractionSubCateList= it.data.types.toMutableList()

                            }



                        }

                    }


                }
            }
        }


        addInfractionViewModel.getSubInfractionTypes(infrTypeID)
    }

    private fun getAddInfection() {

        lifecycleScope.launch {
            addInfractionViewModel.addInfractionStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                         (requireActivity() as MainActivity).showLoader(false)
                         Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data != null) {
                            binding.studentData=it.data.studentDTL

                            binding.tvAdmissionNo.text= buildString {
                                append(getString(R.string.admission_no))
                                append(it.data.studentDTL.admissionNo)
                            }
                            binding.tvClassName.text= buildString {
                                append(getString(R.string.classes))
                                append(it.data.studentDTL.`class`)
                            }
                            binding.tvFatherName.text= buildString {
                                append(getString(R.string.contact_person))
                                append(it.data.studentDTL.contactPerson)
                            }
                            binding.tvContact.text= buildString {
                                append(getString(R.string.contact_no))
                                append(it.data.studentDTL.contactMob)
                            }
                            infractionTypeList.clear()
                            infractionConsequencesList.clear()
                            if (!it.data.infractionTypes.isNullOrEmpty()){

                                infractionTypeList= it.data.infractionTypes.toMutableList()
                            }

                            if (!it.data.infractionConsequences.isNullOrEmpty()){

                                infractionConsequencesList= it.data.infractionConsequences.toMutableList()
                            }

                         }  }
                }
            }
        }

        addInfractionViewModel.addInfraction(studentID)

    }

    private fun popUpSelectInfractionCat(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_infraction_category)
        builder.setView(view)

        relOk.setOnClickListener {
           if (infrTypeSelected){
               binding.tvSelectInfractionCate.text= infractionCatData .infraction


               getSubInfection(infractionCatData.infrTypeID)

               builder.dismiss()

           }
        }

        val infractionCatPopUpListAdapter= InfractionCatPopUpListAdapter(infractionTypeList, object : ItemListener<InfractionType> {
            override fun onItemClick(t: InfractionType, pos: Int, boolean: Boolean) {
                infractionCatData = t
                infrTypeSelected=true
                    }  })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = infractionCatPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun popUpSelectSubInfractionCat(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_infraction_subcategory)
        builder.setView(view)

        relOk.setOnClickListener {
             if (SubInfrTypeSelected){
                 binding.tvSelectSubInfraction.text= subInfractionCatData .infraction

                 addInfractionViewModel.getinfractionInstance(infractionCatData.infrTypeID,
                     subInfractionCatData.infrTypeID,studentID)
                 setInfrenceInstance()
                 builder.dismiss()
             }

        }

        val infractionCatPopUpListAdapter= SubInfractionPopUpListAdapter(subInfractionSubCateList, object : ItemListener<Type> {
            override fun onItemClick(t: Type, pos: Int, boolean: Boolean) {
                subInfractionCatData = t
                SubInfrTypeSelected=true
            }  })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = infractionCatPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun setInfrenceInstance() {

        lifecycleScope.launch {
            addInfractionViewModel.infractionInstanceStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                         binding.tvInstance.text= it.data?.instance.toString()
                    }


                }
            }
        }

    }

    private fun popUpSelectInfractionCons(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_consequences)
        builder.setView(view)

        relOk.setOnClickListener {
           if (infrConsSelected){
               binding.tvSelectCons.text= infractionConsequence .consequences

               builder.dismiss()
           }

        }

        val infractionCatPopUpListAdapter= InfractionConsPopUpListAdapter(infractionConsequencesList, object : ItemListener<InfractionConsequence> {
            override fun onItemClick(t: InfractionConsequence, pos: Int, boolean: Boolean) {
                infractionConsequence = t
                infrConsSelected=true
            }  })

        rvYears.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = infractionCatPopUpListAdapter
        }

        relCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun saveInfraction(action:Int) {
        var isValidate= true
        if (!infrTypeSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_infraction_category))
        }else
        if (!SubInfrTypeSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_infraction_subcategory))
        }else
        if (!infrConsSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_consequences))
        }else
        if (binding.etPlanName.text.toString().isEmpty()){
            isValidate=false
            mainActivity().showMessage(getString( R.string.enter_corrective_action))
        }

        if (isValidate){
            addInfractionViewModel.saveInfraction(
                action,
                studentID,
                subInfractionCatData.infrTypeID,
                infractionConsequence.consID,
                binding.tvInstance.text.toString().toInt(),
                Constant.getCurrentDateTimeSecond(),
                binding.etPlanName.text.toString())

            lifecycleScope.launch {
                addInfractionViewModel.saveInfractionStateFlow.collectLatest {
                    when (it) {
                        is NetworkResult.Loading -> {
                            (requireActivity() as MainActivity).showLoader(true)
                        } is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    } is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        mainActivity().showMessage(getString( R.string.submit_successfully))
                       findNavController().popBackStack()

                    }
                    }
                }
            }

        }







    }


}