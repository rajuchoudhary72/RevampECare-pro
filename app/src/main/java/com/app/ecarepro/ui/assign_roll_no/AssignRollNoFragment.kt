package com.app.ecarepro.ui.assign_roll_no

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.post_roll_no.AssignRollNoBodyItem
import com.app.ecarepro.databinding.FragmentAssignRollNoBinding
import com.app.ecarepro.model.MyClasseTeacherOf
import com.app.ecarepro.model.StudentPro
import com.app.ecarepro.model.StudentRllNo
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssignRollNoFragment : Fragment(), MenuProvider {

    private var studentListArrayList = mutableListOf<StudentRllNo>()
    private lateinit var menuHost: MenuHost
    private var selectedFilterType: Int= Constant.FILTER_NAME
    private lateinit var selectedClassData: MyClasseTeacherOf
    private lateinit var binding :FragmentAssignRollNoBinding
    private val assignRollNoViewModel: AssignRollNoViewModel by viewModels()
    private lateinit var mMyClass: List<MyClasseTeacherOf>

    private   var mMyClassDataString:   ArrayList<String> =  ArrayList( )
    private val nameFilter = listOf("Name",  "Admission" )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAssignRollNoBinding.inflate(inflater,container,false)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
          menuHost  = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

         return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,nameFilter)
        binding.autoCompleteFilter.setAdapter(arrayAdapter)

         binding.autoCompleteFilter.setText("Name",false)


        getMyClass( )

        binding.autoCompleteClass.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                selectedClassData=mMyClass[pos]
                 getStudentListToAssignRollNo()
            }
        binding.autoCompleteFilter.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, pos, id ->
                when(pos){
                    0 ->{
                       selectedFilterType=Constant.FILTER_NAME
                    }

                    1 ->{
                        selectedFilterType=Constant.FILTER_ADMISSION_NO
                    }
                }
                if (selectedClassData!=null){
                     getStudentListToAssignRollNo()
                }
            }


    }

   fun getMyClass(  ){
       lifecycleScope.launch {
           assignRollNoViewModel.classTeacherOfStateFlow.collectLatest {
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
                           if (it.data.myClasses!=null) {
                               mMyClass=it.data.myClasses

                               mMyClass.forEach { data ->
                                   mMyClassDataString.add(data.className.toString())
                               }


                                if (mMyClass!=null && mMyClass.isNotEmpty()){
                                    selectedClassData=mMyClass[0]
                                    binding.autoCompleteClass.setText(selectedClassData.className,false)
                                    if (selectedClassData!=null){
                                        getStudentListToAssignRollNo( )
                                    }
                                }


                               val arrayAdapter= ArrayAdapter(requireContext(), R.layout.view_drop_down_menu,mMyClassDataString)
                               binding.autoCompleteClass.setAdapter(arrayAdapter)
                           }
                       }


                   }


               }
           }
       }
       assignRollNoViewModel.getClassTeacherOf()
   }

    private fun getStudentListToAssignRollNo( ){
         lifecycleScope.launch {
            assignRollNoViewModel.assignRollNoStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                        binding.recyclerAssignRollno.isVisible = false
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerAssignRollno.isVisible = false
                        Log.d("main", "Error$it")
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        binding.recyclerAssignRollno.isVisible = true

                        if (it.data!=null){

                            if (it.data.students!=null){

                                binding.recyclerAssignRollno.isVisible=true
                                binding.tvNoData.isVisible=false


                                studentListArrayList= it.data.students.toMutableList()

                                val assignRollNoListAdapter = AssignRollNoListAdapter(
                                    it.data.students.toMutableList(),
                                    this@AssignRollNoFragment)


                                binding.recyclerAssignRollno.apply {
                                    setHasFixedSize(true)
                                    layoutManager = LinearLayoutManager(activity)
                                    adapter = assignRollNoListAdapter
                                }
                            }else{
                                binding.recyclerAssignRollno.isVisible=false
                                binding.tvNoData.isVisible=true
                            }

                        }

                    }

                    else -> {}
                }
            }
        }
        assignRollNoViewModel.getStudentListToAssignRollNo(selectedClassData.id,selectedFilterType)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_save, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_save -> {
                uploadAssignRollNo()
                true
            }
            else -> false
        }
    }

    private fun uploadAssignRollNo() {

        val requestList = mutableListOf<AssignRollNoBodyItem>()

        studentListArrayList.forEach { d->
            requestList.add(AssignRollNoBodyItem(d.houseID,d.rollNumber,d.stID))
        }
        assignRollNoViewModel.assignRollNumber(requestList).invokeOnCompletion {
            mainActivity().showMessage("Roll Number Assign Successfully")
            menuHost.removeMenuProvider(this)
        }



    }

}