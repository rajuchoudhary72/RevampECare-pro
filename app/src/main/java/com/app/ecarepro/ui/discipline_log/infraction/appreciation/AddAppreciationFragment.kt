package com.app.ecarepro.ui.discipline_log.infraction.appreciation

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentAddAppreciationBinding
import com.app.ecarepro.model.AppreciationReward
import com.app.ecarepro.model.AppreciationType
import com.app.ecarepro.model.InfractionConsequence
import com.app.ecarepro.model.Type
import com.app.ecarepro.model.TypeAppreciation
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.discipline_log.infraction.adapter.AppreciationCatPopUpListAdapter
import com.app.ecarepro.ui.discipline_log.infraction.adapter.AppreciationConsPopUpListAdapter
import com.app.ecarepro.ui.discipline_log.infraction.adapter.SubAppreciationPopUpListAdapter
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAppreciationFragment : Fragment() {


    private var studentID: Int = 0
    private lateinit var subAppreciationCatData: TypeAppreciation
    private lateinit var subAppreciationSubCateList: List<TypeAppreciation>
    private lateinit var appreciationReward: AppreciationReward
    private var apprecTypeSelected: Boolean = false
    private var SubApprecTypeSelected: Boolean = false
    private var apprecRewardSelected: Boolean = false
    private lateinit var appreciationCatData: AppreciationType
    private lateinit var appreciationRewardList: List<AppreciationReward>
    private lateinit var appreciationTypeList: List<AppreciationType>
     private val addAppreciationViewModel : AddAppreciationViewModel by viewModels()
    private lateinit var binding : FragmentAddAppreciationBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding=FragmentAddAppreciationBinding.inflate(inflater,container,false)
        studentID=  requireArguments().getInt(Constant.STUDENT_ID_ARGUMENT)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.apply {
                title = "Add Appreciation"

                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }

        binding.tvSelectApprecCate.setOnClickListener {
            popUpSelectAppreciationCat()
        }

        binding.tvSelectSubApprec.setOnClickListener {
            popUpSelectSubAppreciationCat()
        }

        binding.tvSelectReward.setOnClickListener {
            popUpSelectAppreciationReward()
        }

        binding.tvContinue.setOnClickListener {
            saveAppreciation(1)
        }
        binding.tvContinueNoti.setOnClickListener {
            saveAppreciation(2)
        }
        getAddAppreciation()
    }


    private fun getSubAppreciation(aprID: Int) {

        lifecycleScope.launch {
            addAppreciationViewModel.subAppreciationTypesStateFlow.collectLatest {
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

                            subAppreciationSubCateList=it.data.types



                        }

                    }


                }
            }
        }


        addAppreciationViewModel.subAppreciationTypes(aprID)
    }

    private fun getAddAppreciation() {

        lifecycleScope.launch {
            addAppreciationViewModel.addAppreciationStateFlow.collectLatest {
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

                            appreciationTypeList=it.data.appreciationTypes
                            appreciationRewardList=it.data.appreciationRewards  }  }
                }
            }
        }

        addAppreciationViewModel.addAppreciation(studentID)

    }

    private fun popUpSelectAppreciationCat(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_apprec_category)
        builder.setView(view)

        relOk.setOnClickListener {
            binding.tvSelectApprecCate.text= appreciationCatData .appreciation
            apprecTypeSelected=true

            getSubAppreciation(appreciationCatData.aprID)

            builder.dismiss()

        }

        val infractionCatPopUpListAdapter= AppreciationCatPopUpListAdapter(appreciationTypeList, object : ItemListener<AppreciationType> {
            override fun onItemClick(t: AppreciationType, pos: Int, boolean: Boolean) {
                appreciationCatData = t
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

    private fun popUpSelectSubAppreciationCat(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_apprec_subcategory)
        builder.setView(view)

        relOk.setOnClickListener {
            binding.tvSelectSubApprec.text= subAppreciationCatData.appreciation
            SubApprecTypeSelected=true
            addAppreciationViewModel.appreciationInstance( subAppreciationCatData.aprSubID,studentID)
            setInfrenceInstance()
            builder.dismiss()

        }

        val infractionCatPopUpListAdapter= SubAppreciationPopUpListAdapter(subAppreciationSubCateList, object : ItemListener<TypeAppreciation> {
            override fun onItemClick(t: TypeAppreciation, pos: Int, boolean: Boolean) {
                subAppreciationCatData = t
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
            addAppreciationViewModel.appreciationInstanceStateFlow.collectLatest {
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

    private fun popUpSelectAppreciationReward(){

        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog) .create()
        val view = layoutInflater.inflate(R.layout.custom_popup_select_class,null)
        val  relCancel = view.findViewById<RelativeLayout>(R.id.rel_cancel)
        val  relOk = view.findViewById<RelativeLayout>(R.id.rel_ok)
        val  rvYears = view.findViewById<RecyclerView>(R.id.rv_year)
        val  tvHeading = view.findViewById<TextView>(R.id.tv_heading)
        tvHeading.text=getString(R.string.select_consequences)
        builder.setView(view)

        relOk.setOnClickListener {
            binding.tvSelectReward.text= appreciationReward .reward
            apprecRewardSelected=true
            builder.dismiss()

        }

        val infractionCatPopUpListAdapter= AppreciationConsPopUpListAdapter(appreciationRewardList, object : ItemListener<AppreciationReward> {
            override fun onItemClick(t: AppreciationReward, pos: Int, boolean: Boolean) {
                appreciationReward = t
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

    private fun saveAppreciation(action:Int) {
        var isValidate= true
        if (!apprecTypeSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_apprec_category))
        }else
        if (!SubApprecTypeSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_apprec_subcategory))
        }else
        if (!apprecRewardSelected){
            isValidate=false
            mainActivity().showMessage(getString( R.string.select_reward))
        }else
        if (binding.etRemark .text.toString().isEmpty()){
            isValidate=false
            mainActivity().showMessage(getString( R.string.enter_remark))
        }

        if (isValidate){
            addAppreciationViewModel.saveAppreciation(
                action,
                studentID,
                subAppreciationCatData.aprSubID,
                appreciationReward.rwdID,
                binding.tvInstance.text.toString().toInt(),
                Constant.currentDate(),
                binding.etRemark.text.toString())

            lifecycleScope.launch {
                addAppreciationViewModel.saveAppreciationStateFlow.collectLatest {
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