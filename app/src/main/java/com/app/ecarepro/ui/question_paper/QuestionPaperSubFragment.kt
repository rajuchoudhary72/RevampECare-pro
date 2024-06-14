package com.app.ecarepro.ui.question_paper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.QuestionPaper
import com.app.ecarepro.databinding.FragmentQuestionPaperSubBinding
import com.app.ecarepro.utils.AndroidDownloader
import com.app.ecarepro.utils.Constant
import com.app.ecarepro.utils.listener.ItemListener


class QuestionPaperSubFragment( val questionPapers: List<QuestionPaper>) : Fragment(),ItemListener<QuestionPaper>  {

    private lateinit var binding: FragmentQuestionPaperSubBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentQuestionPaperSubBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (questionPapers!=null){



                val questionPaperAdapter =
                    QuestionPaperAdapter(questionPapers,
                        this@QuestionPaperSubFragment)

                binding.rvQuestionPaper.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = questionPaperAdapter
                }
                binding.rvQuestionPaper.isVisible=true
                binding.tvNoData.isVisible=false


            }else{
                binding.rvQuestionPaper.isVisible=false
                binding.tvNoData.isVisible=true

            }
        }

    }

    override fun onItemClick(t: QuestionPaper, pos: Int, boolean: Boolean) {
        if (pos==1){
              findNavController().navigate(R.id.openPdfFragment, Bundle().apply {
                putString(Constant.url, t.file)
            })
        }
        if (pos==2){
            val androidDownloader = AndroidDownloader(requireContext())
            androidDownloader.downloadFile(t.file, getString(R.string.question_paper) )
        }
    }
}