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
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddInfractionFragment : Fragment() {

    private var studentID: Int = 0
    private lateinit var subInfractionCatData: Type
    private lateinit var subInfractionSubCateList: List<Type>
    private lateinit var infractionConsequence: InfractionConsequence
    private var infrTypeSelected: Boolean = false
    private var SubInfrTypeSelected: Boolean = false
    private var infrConsSelected: Boolean = false
    private lateinit var infractionCatData: InfractionType
    private lateinit var infractionConsequencesList: List<InfractionConsequence>
    private lateinit var infractionTypeList: List<InfractionType>
    private lateinit var binding: FragmentAddInfractionBinding
    private val addInfractionViewModel : AddInfractionViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentAddInfractionBinding.inflate(inflater,container,false)
          studentID=  requireArguments().getInt(Constant.STUDENT_ID_ARGUMENT)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.apply {
                title = "Add Infraction"

                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }

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

                            subInfractionSubCateList=it.data.types



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

                            infractionTypeList=it.data.infractionTypes
                            infractionConsequencesList=it.data.infractionConsequences  }  }
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
            binding.tvSelectInfractionCate.text= infractionCatData .infraction
            infrTypeSelected=true

            getSubInfection(infractionCatData.infrTypeID)

             builder.dismiss()

        }

        val infractionCatPopUpListAdapter= InfractionCatPopUpListAdapter(infractionTypeList, object : ItemListener<InfractionType> {
            override fun onItemClick(t: InfractionType, pos: Int, boolean: Boolean) {
                infractionCatData = t
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
            binding.tvSelectSubInfraction.text= subInfractionCatData .infraction
            SubInfrTypeSelected=true
            addInfractionViewModel.getinfractionInstance(infractionCatData.infrTypeID,
                subInfractionCatData.infrTypeID,448)
            setInfrenceInstance()
            builder.dismiss()

        }

        val infractionCatPopUpListAdapter= SubInfractionPopUpListAdapter(subInfractionSubCateList, object : ItemListener<Type> {
            override fun onItemClick(t: Type, pos: Int, boolean: Boolean) {
                subInfractionCatData = t
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
            binding.tvSelectCons.text= infractionConsequence .consequences
            infrConsSelected=true
            builder.dismiss()

        }

        val infractionCatPopUpListAdapter= InfractionConsPopUpListAdapter(infractionConsequencesList, object : ItemListener<InfractionConsequence> {
            override fun onItemClick(t: InfractionConsequence, pos: Int, boolean: Boolean) {
                infractionConsequence = t
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
        }
        if (!SubInfrTypeSelected){
            isValidate=false
        }
        if (!infrConsSelected){
            isValidate=false
        }
        if (binding.etPlanName.text.toString().isEmpty()){
            isValidate=false
        }

        if (isValidate){
            addInfractionViewModel.saveInfraction(
                action,
                studentID,
                subInfractionCatData.infrTypeID,
                infractionConsequence.consID,
                binding.tvInstance.text.toString().toInt(),
                Constant.currentDate(),
                binding.etPlanName.text.toString())  }

        lifecycleScope.launch {
            addInfractionViewModel.saveInfractionStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    } is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                     } is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                   /* findNavController().navigate(R.id.action_addInfractionFragment_to_infractionListFragment,Bundle( ).apply {
                        putInt(Constant.STUDENT_ID_ARGUMENT, studentID)
                    })*/
                     }
                 }
            }
        }





    }


}