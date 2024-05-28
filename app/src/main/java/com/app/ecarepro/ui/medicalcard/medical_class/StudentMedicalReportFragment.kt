package com.app.ecarepro.ui.medicalcard.medical_class

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.MedicalReportBinding
import com.app.ecarepro.model.Student
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.medicalcard.MedicineCardViewModel
import com.app.ecarepro.utils.PdfDocumentAdapter
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class StudentMedicalReportFragment : Fragment() {
    private lateinit var binding: MedicalReportBinding
    private val mViewModel: MedicineCardViewModel by viewModels()
    private val studentList = mutableListOf<Student>()


    val bundle by lazy {
        arguments
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            Picasso.setSingletonInstance(
                Picasso.Builder(requireActivity()) // additional settings
                    .build()
            )
        }catch (e:IllegalStateException){

        }
        binding = MedicalReportBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            mViewModel._studentMedicalCardResponse.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        // binding.rvMedicineIssue.isVisible = false
                        Log.d("main", "Error$it")
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        it.data?.let {student->
                            student.profile?.let { it1 -> student.immunizationRecords?.let { it2 ->
                                showData(it1,
                                    it2
                                )
                            } }
                        }

                    }
                }
            }

        }
        bundle?.let {
            val id = it.getString("ID")
            if (id != null) {
                mViewModel.getMedicalCard(id)
            }
        }


    }

    private val DEFAULT_TEXT = ""
    private fun showData(details: Profile, immunizationRecords: ImmunizationRecords) {
        getIcNoProfileBig(requireContext())?.let {
            getIcNoProfileBig(requireContext())?.let { it1 ->
                Picasso.get()
                    .load( details.photo)
                    .placeholder(it)
                    .error(it1)
                    .into(binding.logo)
            }
        }
        //binding.textSchoolName.text = schoolInfo.schoolName ?: DEFAULT_TEXT
        binding.textStudentName.text = details.name ?: DEFAULT_TEXT
        binding.textClass.text = details.className ?: DEFAULT_TEXT
        binding.textFather.text = details.fatherName ?: DEFAULT_TEXT
        binding.textBloodgroup.text = details.bloodGroup ?: DEFAULT_TEXT
        binding.textFatherContact.text = details.fatherMob_1 ?: DEFAULT_TEXT
        binding.textDateBirth.text = details.dob ?: DEFAULT_TEXT
        binding.textMother.text = details.motherName ?: DEFAULT_TEXT
        binding.textMotherContact.text = details.motherMob_1 ?: DEFAULT_TEXT
        binding.textBGC.text = immunizationRecords.bcg ?: DEFAULT_TEXT
        binding.textDiphtheria.text = immunizationRecords.diphtheria ?: DEFAULT_TEXT
        binding.textCough.text = immunizationRecords.whoopingCough ?: DEFAULT_TEXT
        binding.textTetanus.text = immunizationRecords.tetanus ?: DEFAULT_TEXT
        binding.textMeasles.text = immunizationRecords.measles ?: DEFAULT_TEXT
        binding.textMMR.text = immunizationRecords.mmr ?: DEFAULT_TEXT
        binding.textHepatitisB.text = immunizationRecords.hepatitisB ?: DEFAULT_TEXT
        binding.textTyphoid.text = immunizationRecords.typhoid ?: DEFAULT_TEXT
        binding.textChicken.text = immunizationRecords.chicken_pox ?: DEFAULT_TEXT
        binding.textHecatitisA.text = immunizationRecords.hepatitisA ?: DEFAULT_TEXT
        binding.textDose1.text = immunizationRecords.covidDose1 ?: DEFAULT_TEXT
        binding.tvAllergeis.text = immunizationRecords.allergies ?: DEFAULT_TEXT
        binding.textDose2.text = immunizationRecords.covidDose2 ?: DEFAULT_TEXT
        binding.textBooster.text = immunizationRecords.covidBoosterDose ?: DEFAULT_TEXT
        binding.textDPTBooster.text = immunizationRecords.dptBooster ?: DEFAULT_TEXT
        binding.tvSpecPast.text = immunizationRecords.specificPastDisease ?: DEFAULT_TEXT
        binding.tvSurgeryPast.text = immunizationRecords.surgeryUndergoneInthePast ?: DEFAULT_TEXT
        binding.tvOther.text = immunizationRecords.childRegularMedication ?: DEFAULT_TEXT

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("size", " ${binding.scrollView.width}  ${binding.scrollView.width}")
            val bitmap = getBitmapFromView(binding.scrollView, binding.scrollView.getChildAt(0).height, binding.scrollView.getChildAt(0).width)
            val pdfFile = createPdf(bitmap)
            val printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = PdfDocumentAdapter(requireActivity(), pdfFile?.absolutePath ?: "")
            printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
        }, 500)
    }

    private fun createPdf(bit: Bitmap): File? {
        return try {
            val wm = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displaymetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(displaymetrics)
            val hight = 3508f
            val width = 2500f
            val convertHighet = hight.toInt()
            val convertWidth = width.toInt()

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            paint.color = Color.WHITE
            canvas.drawPaint(paint)

            val bitmap = Bitmap.createScaledBitmap(bit, convertWidth, convertHighet, true)
            paint.color = Color.BLUE
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)

            val pdfPath = requireActivity().filesDir.absolutePath + "/report.pdf"
            val camFile = File(pdfPath)
            if (camFile.exists()) {
                camFile.delete()
            }
            camFile.createNewFile()
            try {
                document.writeTo(FileOutputStream(camFile))

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Something wrong: $e", Toast.LENGTH_LONG).show()
            }

            document.close()
            camFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }
    fun getIcNoProfileBig(context: Context): VectorDrawableCompat? {
        return VectorDrawableCompat.create(context.resources, R.drawable.ic_no_profile_big, null)
    }

}