package com.app.ecarepro.ui.message.selectRecipients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.StaffType
import com.app.ecarepro.data.network.model.StaffTypeDto
import com.app.ecarepro.databinding.FragmentSelectStaffTypeBinding
import com.app.ecarepro.staffType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectStaffTypesFragment : DialogFragment() {

    private var _binding: FragmentSelectStaffTypeBinding? = null
    private val binding get() = _binding!!

    private val selectStaffTypes = mutableListOf<StaffType>()

    private var onContactSelected: ((List<StaffType>) -> Unit)? = null

    fun onContactSelected(onContactSelected: (List<StaffType>) -> Unit): SelectStaffTypesFragment {
        this@SelectStaffTypesFragment.onContactSelected = onContactSelected
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectStaffTypeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnDone.setOnClickListener {
            onContactSelected?.invoke(selectStaffTypes)
            dismiss()
        }

        binding.viewPager.withModels {
            (arguments?.getSerializable(STAFF_TYPES) as StaffTypeDto).let { dto ->
                selectStaffTypes.addAll(dto.selectedStaffType ?: emptyList())
                dto.staffType?.forEach { type ->
                    staffType {
                        id(type.staffTypeID)
                        isChecked(selectStaffTypes.contains(type))
                        text(type.staffType)
                        clickListener { _ ->
                            if (selectStaffTypes.contains(type)) {
                                selectStaffTypes.remove(type)
                            } else {
                                selectStaffTypes.add(type)
                            }
                            this@withModels.requestModelBuild()
                        }
                    }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val STAFF_TYPES = "staff_types"
        fun getInstance(staffTypeDto: StaffTypeDto) = SelectStaffTypesFragment().apply {
            arguments = bundleOf(
                STAFF_TYPES to staffTypeDto
            )
        }
    }

}

