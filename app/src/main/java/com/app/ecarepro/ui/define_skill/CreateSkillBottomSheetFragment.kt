package com.app.ecarepro.ui.define_skill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.databinding.FragmentCreateSkillBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateSkillBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCreateSkillBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateSkillViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateSkillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryDropdown()
        setupObservers()
        setupListeners()
    }

    private fun setupCategoryDropdown() {
        val categories = viewModel.categories ?: emptyList()
        val categoryNames: List<String> = categories.map { it.category ?: "" }
        val categoryAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categoryNames)
        (binding.autoCategory as? AutoCompleteTextView)?.setAdapter(categoryAdapter)

        binding.autoCategory.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categories[position]
            viewModel.loadSkillTypes(selectedCategory.sklCatID)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel
                .skillTypes
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                .collectLatest { types ->
                    val typeNames = types.map { it.type }
                    val typeAdapter =
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            typeNames
                        )
                    (binding.autoType as? AutoCompleteTextView)?.setAdapter(typeAdapter)
                    binding.autoType.setOnItemClickListener { _, _, position, _ ->
                        val selectedType = types[position]
                    }
                }

        }

    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            val category = binding.autoCategory.text.toString()
            val type = binding.autoType.text.toString()
            val skillName = binding.etSkillName.text.toString()

            // Add validation if needed
            if (category.isBlank() || type.isBlank() || skillName.isBlank()) {
                // Show error or toast
                return@setOnClickListener
            }

            // TODO: Trigger save action
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SKILL_CATEGORIES = "skill_categories"
        fun newInstance(skillCategories: List<Category>): CreateSkillBottomSheetFragment {
            return CreateSkillBottomSheetFragment().apply {
                arguments = bundleOf(SKILL_CATEGORIES to skillCategories)
            }
        }
    }
}
