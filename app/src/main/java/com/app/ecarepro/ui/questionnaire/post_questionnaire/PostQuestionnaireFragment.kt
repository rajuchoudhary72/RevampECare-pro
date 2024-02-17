package com.app.ecarepro.ui.questionnaire.post_questionnaire

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.app.ecarepro.AddMoreFavouritesBindingModelBuilder
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentPostQustionnaireBinding
import com.app.ecarepro.ui.questionnaire.answer_details.AnswerDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostQuestionnaireFragment : Fragment() {

    private lateinit var  binding : FragmentPostQustionnaireBinding
    private val viewMode : PostQuestionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {

        binding= FragmentPostQustionnaireBinding.inflate(inflater,container,false)

         return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


         binding.textFiledThoughts.doAfterTextChanged {
             if (it != null) {
                 binding.btnAdd.isEnabled = it.isNotEmpty()
             }
         }

        binding.btnAdd.setOnClickListener {
            viewMode.addQuestion(binding.textFiledThoughts.text.toString(),
                "","","")
        }

    }
}