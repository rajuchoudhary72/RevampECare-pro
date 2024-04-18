package com.app.ecarepro.ui.award

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentExcellenceAwardBinding
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.PdfDocumentAdapter


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ExcellenceAwardFragment : Fragment() {
    private lateinit var binding: FragmentExcellenceAwardBinding
    private val mViewModel: ExcellenceAwardViewModel by viewModels()
    private val sportsAdapter by lazy { SportsAdapter() }
    private val academicAdapter by lazy { SportsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExcellenceAwardBinding.inflate(inflater, container, false)
        binding.revSports.adapter = sportsAdapter
        binding.recacAdemic.adapter = academicAdapter

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        mViewModel.getAwardData()
        binding.tvPrint.setOnClickListener {
                createPDF()

        }
    }

    private fun observers() {
        lifecycleScope.launch {
            mViewModel._awadStateFlow.collectLatest {
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
                            it.data.sportActivity?.let { it1 -> setSportsItems(it1) }
                            it.data.academicActivity?.let { it1 -> setAcademicAdapterItems(it1) }
                            updateUI(it.data)
                        }

                    }

                }
            }
        }
    }

    private fun setSportsItems(sportActivity: List<SportActivity?>) {
        val listSports = mutableListOf<Pair<String, String>>()
        for (sport in sportActivity) {
            sport?.let {
                listSports.add(Pair(it.activity, it.marks))
            }

        }
        sportsAdapter.addItems(listSports)
    }

    private fun setAcademicAdapterItems(sportActivity: List<AcademicActivity?>) {
        val listSports = mutableListOf<Pair<String, String>>()
        for (sport in sportActivity) {
            sport?.let {
                listSports.add(Pair(it.activity, it.marks))
            }

        }
        academicAdapter.addItems(listSports)
    }

    private fun updateUI(response: ExcellenceAwardResponse) = with(binding) {
        data = response
        invalidateAll()
    }

    private fun createPDF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Above Android O, use PixelCopy
            val bitmap = Bitmap.createBitmap(binding.nestScroll.width, binding.nestScroll.height, Bitmap.Config.ARGB_8888)
            val location = IntArray(2)
            binding.nestScroll.getLocationInWindow(location)
            PixelCopy.request(requireActivity().window,
                Rect(location[0], location[1], location[0] + binding.nestScroll.width, location[1] + binding.nestScroll.height),
                bitmap,
                {
                    if (it == PixelCopy.SUCCESS) {
                        val file = createPdf(bitmap)
                        file?.let {
                            val pdfFile: File = it
                            val printManager =
                                requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
                            val printAdapter: PrintDocumentAdapter =
                                PdfDocumentAdapter(requireActivity(), pdfFile.absolutePath)
                            printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
                        }
                    }
                },
                Handler(Looper.getMainLooper()) )
        } else {
            val tBitmap = Bitmap.createBitmap(
                binding.nestScroll.width, binding.nestScroll.height, Bitmap.Config.RGB_565
            )
            val canvas = Canvas(tBitmap)
            binding.nestScroll.draw(canvas)
            canvas.setBitmap(null)
            val file = createPdf(tBitmap)
            file?.let {
                val pdfFile: File = it
                val printManager =
                    requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapter: PrintDocumentAdapter =
                    PdfDocumentAdapter(requireActivity(), pdfFile.absolutePath)
                printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
            }
        }

    }

    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private fun createPdf(bit: Bitmap): File? {
        try {
            val wm = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displaymetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
            val hight = 3508f
            val width = 2500f
            val convertHighet = hight.toInt()
            val convertWidth = width.toInt()
            val document = PdfDocument()
            val pageInfo = PageInfo.Builder(convertWidth, convertHighet, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            paint.color = Color.WHITE
            canvas.drawPaint(paint)
            val bitmap = Bitmap.createScaledBitmap(bit, convertWidth, convertHighet, true)
            paint.color = Color.BLUE
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)

            val pdfPath: String = requireActivity().getFilesDir().getAbsolutePath() + "/report.pdf"
            val camFile = File(pdfPath)
            if (camFile.exists()) {
                camFile.delete()
            }
            camFile.createNewFile()
            try {
                document.writeTo(FileOutputStream(camFile))
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireActivity(), "Something wrong: $e", Toast.LENGTH_LONG).show()
            }

            // close the document
            document.close()
            return camFile
            //  Toast.makeText(this, "PDF of Scroll is created!!!", Toast.LENGTH_SHORT).show();
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}