package com.app.ecarepro.ui.language

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.R
import com.app.ecarepro.databinding.FragmentLanguageSelectBinding
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class LanguageSelect : Fragment() {

    private lateinit var binding: FragmentLanguageSelectBinding

    private val viewModel: LanguageViewModel by viewModels()

    private val languageList = mutableListOf<LanguageModel>(
        LanguageModel("English" ,"en"),
        LanguageModel("Hindi" ,"hi")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageSelectBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = getString(R.string.language_select)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setUpLocal()

        lifecycleScope.launch {
            // Update UI when language changes
            viewModel.selectedLanguage.collect { language ->
                updateUITexts()
            }
        }

        binding.recyclerViewLanguages.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.selectedLanguage.collect { selectedLangCode ->
               val adapter = LanguageAdapter(languageList, selectedLangCode,) { selectedLang ->
                   val langCode = selectedLang ?: "en"
                   viewModel.changeLanguage(langCode)

                 //  Toast.makeText(requireContext(), "Language set to: $selectedLang", Toast.LENGTH_SHORT).show()

//                   // Restart app to apply language
//                   requireActivity().let {
//                       //LanguageManager.languageSetAndRestartApp(it, langCode)
//                       setUpLocal(langCode)
//                   }

                   LanguageManager.setNewLocale(requireContext(), langCode) // Switch to Hindi

                   val intent = Intent(requireActivity(), MainActivity::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                   startActivity(intent)
                   requireActivity().finish()

               }
                binding.recyclerViewLanguages.adapter = adapter
            }
        }

    }


        private fun updateUITexts() {
            lifecycleScope.launch {
                // Example of updating UI texts based on localization
              //  binding.includeToolbar.toolbarTitle.text= localizationViewModel.getString("language_select")
            }
        }


    fun setUpLocal(lan:String){
        val local = Locale(lan)
        Locale.setDefault(local)
        val config = resources.configuration
        config.locale = local
        resources.updateConfiguration(config, resources.displayMetrics)
        applyLanguageToActivity(requireActivity())
    }

    fun applyLanguageToActivity(activity: Activity) {
        activity.finish() // Finish current activity
        activity.overridePendingTransition(0, 0) // Prevent animation flicker
        activity.startActivity(activity.intent) // Restart activity
    }

    fun applyLanguage(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

}