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
import com.app.ecarepro.R
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.databinding.FragmentLanguageSelectBinding
import com.app.ecarepro.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class LanguageSelect : Fragment() {

    private lateinit var binding: FragmentLanguageSelectBinding

    private val viewModel: LanguageViewModel by viewModels()

    private val languages = mapOf(
        "English" to "en",
        "Hindi" to "hi",
        "Bengali" to "bn",
        "Telugu" to "te",
        "Marathi" to "mr",
        "Tamil" to "ta",
        "Gujarati" to "gu",
        "Kannada" to "kn",
        "Odia" to "or",
        "Punjabi" to "pa"
    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageSelectBinding.inflate(inflater, container, false)
        binding.includeToolbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.includeToolbar.toolbarTitle.text = "Language Select"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setUpLocal()

        binding.recyclerViewLanguages.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.selectedLanguage.collect { selectedLangCode ->
               val adapter = LanguageAdapter(languages.keys.toList(), selectedLangCode) { selectedLang ->
                   val langCode = languages[selectedLang] ?: "en"
                   viewModel.changeLanguage(langCode)

                   Toast.makeText(requireContext(), "Language set to: $selectedLang", Toast.LENGTH_SHORT).show()

                   // Restart app to apply language
                   requireActivity().let {
                       LanguageManager.languageSetAndRestartApp(it, langCode)
                   }

               }
                binding.recyclerViewLanguages.adapter = adapter
            }
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