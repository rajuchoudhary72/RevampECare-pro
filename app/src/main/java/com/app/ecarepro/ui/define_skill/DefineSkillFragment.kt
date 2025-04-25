package com.app.ecarepro.ui.define_skill

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.Skill
import com.app.ecarepro.databinding.FragmentDefineSkillBinding
import com.app.ecarepro.defineSkill
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DefineSkillFragment : Fragment() {

    private var _binding: FragmentDefineSkillBinding? = null
    private val binding get() = _binding!!

    private val defineSkillViewModel: DefineSkillViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDefineSkillBinding.inflate(inflater, container, false).apply {
            viewModel = defineSkillViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            defineSkillViewModel
                .uiState
                .flowWithLifecycle(
                    lifecycle = viewLifecycleOwner.lifecycle,
                    Lifecycle.State.CREATED
                )
                .collectLatest { uiState ->
                    handleLoadingAndErrorState(uiState)
                    if (uiState is DefineSkillUiState.Success) {
                        handleSuccessState(uiState)
                    }
                }
        }

    }

    private fun setUpViews() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnCreateSkill.setOnClickListener {
                openCreateSkillBottomSheet()
            }
        }
    }

    private fun openCreateSkillBottomSheet(skill: Skill? = null) {
        val uiState = defineSkillViewModel.uiState.value
        if (uiState is DefineSkillUiState.Success)
            CreateSkillBottomSheetFragment
                .newInstance(uiState.skillCategory, skill)
                .show(childFragmentManager, "CreateSkillBottomSheetFragment")
    }

    private fun handleLoadingAndErrorState(uiState: DefineSkillUiState) {
        mainActivity().showLoader(uiState.isLoading())
        uiState.getErrorOrNull()
            ?.let { mainActivity().showMessage(it.message ?: UNKNOWN_ERROR_MESSAGE) }
    }

    private fun handleSuccessState(uiState: DefineSkillUiState.Success) {
        buildSkillCategoryDropDown(uiState.skillCategory)
        buildSkillList(uiState.skills)
    }

    private fun buildSkillCategoryDropDown(skillCategory: List<Category>) {
        val categoryNames = skillCategory.map { it.category }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categoryNames
        )

        binding.selectSkill.let {
            it.setAdapter(adapter)
            it.setOnItemClickListener { _, _, position, _ ->
                val selectedCategory = skillCategory[position]
                defineSkillViewModel.onCategorySelected(selectedCategory.sklCatID)
            }
        }

    }

    private fun buildSkillList(skills: List<Skill>) {
        binding.recyclerView.withModels {
            skills.forEachIndexed { index, skill ->
                defineSkill {
                    id(skill.id)
                    index(index.plus(1))
                    skill(skill)
                    onClickEdit { _ ->
                        openCreateSkillBottomSheet(skill)
                    }
                    onClickDelete { _ ->
                        val alertDialogBuilder = AlertDialog.Builder(requireContext())
                        alertDialogBuilder.setTitle("Delete Skill")
                        alertDialogBuilder.setMessage("Are you sure you want to delete this skill?")
                        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                            defineSkillViewModel.deleteSkill(skill) {
                                mainActivity().showMessage(it)
                            }

                            dialog.dismiss()
                        }
                        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialogBuilder.create().show()
                    }

                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}