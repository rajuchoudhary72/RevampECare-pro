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
import com.app.ecarepro.data.network.model.Skill
import com.app.ecarepro.databinding.FragmentCreateSkillBinding
import com.app.ecarepro.ui.mainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

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
            mainActivity().showLoader(true)
            loadSkillTypes(selectedCategory.sklCatID)
        }
        viewModel.skill?.let {
            binding.autoCategory.setText(categories.find { it.sklCatID == viewModel.skill?.sklCatID }?.category, false)
            binding.etSkillName.setText(viewModel.skill?.skill)
            loadSkillTypes(it.sklCatID)
        }
    }

    private fun loadSkillTypes(sklCatID:Int) {
        viewModel.loadSkillTypes(sklCatID) { message ->
            mainActivity().showLoader(false)
            binding.autoType.setText("")
            message?.let {
                mainActivity().showMessage(message)
            }
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

                    viewModel.skill?.let {skill ->
                        binding.autoType.setText(types.firstOrNull { it.sklTypeID == skill.sklTypeID }?.type, false)
                    }
                }

        }

    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            val category = binding.autoCategory.text.toString()
            val type = binding.autoType.text.toString()
            val skillName = binding.etSkillName.text.toString()

            if (category.isBlank() || type.isBlank() || skillName.isBlank()) {
                mainActivity().showMessage("Please fill in all fields.")
                return@setOnClickListener
            }

            viewModel.saveSkill(category, type, skillName) { success, message ->
                mainActivity().showMessage(message)
                if (success) {
                    dismiss()
                }
            }
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
        const val SKILL = "skill"
        fun newInstance(skillCategories: List<Category>, skill: Skill?): CreateSkillBottomSheetFragment {
            return CreateSkillBottomSheetFragment().apply {
                arguments = bundleOf(SKILL_CATEGORIES to skillCategories, SKILL to skill)
            }
        }
    }
}
