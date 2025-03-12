package com.app.ecarepro.ui.appuserreport

import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.databinding.AppUserReportBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.io.File
import java.io.FileOutputStream
import java.io.IOException



class UserRepostFragment : Fragment() {
    private lateinit var binding: AppUserReportBinding
    private val barEntriesG1 = ArrayList<BarEntry>()
    private val barEntries = ArrayList<BarEntry>()
    private val xAxisLabel = ArrayList<String>()
    private var userType = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.app_user_report, container, false)
        bindDataWithUi()
        initListener()
        return binding.root
    }



    protected fun initListener() {
        binding.tvGetReport.setOnClickListener {
            try {
                val bundle=Bundle()
                bundle.putString("userType","$userType")
                findNavController().navigate(R.id.appUserReportWebFragment,bundle)

                //&& ((AppUserReportActivity) getActivity()).isStoragePermission())
                //hitAppUserReportAPi();
            } catch (ex: Exception) {
                ex.stackTrace
            }
        }
    }

    private fun bindDataWithUi()= with(binding) {
        val bundle: Bundle? = arguments
        if (null != bundle) {
            tvGetReport.visibility = View.VISIBLE
            val deviceUsersArrayList= bundle.getParcelableArrayList<DeviceUser>("DeviceInfo")
            userType = bundle.getInt("userType", -1)
            if (deviceUsersArrayList != null) {
                if (userType == 0) {
                    barChartView.visibility = View.VISIBLE
                    pieChartView.visibility = View.GONE
                    tvLblNoUser.visibility = View.VISIBLE
                    setData(deviceUsersArrayList)
                } else {
                    pieChartView.visibility = View.VISIBLE
                    barChartView.visibility = View.GONE
                    tvLblNoUser.visibility = View.GONE
                    tvTotalUser.visibility = View.GONE
                    for (i in deviceUsersArrayList.indices) {
                        if (deviceUsersArrayList[i].userType == userType) {
                            bindData(deviceUsersArrayList[i])
                        }
                    }
                }
            }
        }
    }

    private fun bindData(deviceUsers: DeviceUser) {
        setPieData(deviceUsers)
    }

    private fun setCalculatedPercentage(user: Int, totalUser: Int): String {
        return if (totalUser != 0) (Math.round(user * 100.00 / totalUser * 100.00) / 100.00).toString() + "%" else "0.0%"
    }

    private fun setPieData(deviceUsers: DeviceUser) = with(binding){

        tvAndroidCount.text = "${deviceUsers.android}"
        tvIosCount.setText(deviceUsers.iOS.toString())
        tvBothCount.setText(deviceUsers.both.toString())
        tvRemainingCount.setText(deviceUsers.remaining.toString())
        tvAndroidPer!!.text =
            setCalculatedPercentage(deviceUsers.android, deviceUsers.totalUser)
        tvIosPer!!.text = setCalculatedPercentage(deviceUsers.iOS, deviceUsers.totalUser)
        tvBothPer!!.text =
            setCalculatedPercentage(deviceUsers.both, deviceUsers.totalUser)
        tvRemainingPer!!.text =
            setCalculatedPercentage(deviceUsers.remaining, deviceUsers.totalUser)
        pieChartView.setUsePercentValues(true)
        //pieChart.setUsePercentValues(false);
        pieChartView.setRotationEnabled(false)
        pieChartView.setDrawMarkerViews(false)
        val yvalues = ArrayList<PieEntry>()
        yvalues.add(PieEntry(deviceUsers.android.toFloat(), 0))
        yvalues.add(PieEntry(deviceUsers.iOS.toFloat(), 1))
        yvalues.add(PieEntry(deviceUsers.both.toFloat(), 2))
        yvalues.add(PieEntry(deviceUsers.remaining.toFloat(), 3))
        val dataSet = PieDataSet(yvalues, "")
        dataSet.sliceSpace = 1f
        val xVals = ArrayList<String>()
        xVals.add("")
        xVals.add("")
        val data = PieData(dataSet)
        pieChartView.setData(data)
        dataSet.setColors(
            getResources().getColor(R.color.android),
            getResources().getColor(R.color.ios),
            getResources().getColor(R.color.button_browse_green),
            getResources().getColor(R.color.light_grey)
        )
        data.setValueTextSize(12f)
        data.setDrawValues(false)
        pieChartView.getLegend().isEnabled = false
        pieChartView.animateXY(1400, 1400)
        val s: String = deviceUsers.totalUser.toString() + "\nTotal "
        val length: String = deviceUsers.totalUser.toString() + ""
        val ss1 = SpannableString(s)
        ss1.setSpan(RelativeSizeSpan(2f), 0, length.length, 0) // set size
        ss1.setSpan(
            ForegroundColorSpan(getResources().getColor(R.color.black)),
            0,
            3,
            0
        ) // set color
        pieChartView.setCenterText(ss1)
        pieChartView.setCenterTextSize(16f)
        pieChartView.setCenterTextColor(getResources().getColor(R.color.black))
        pieChartView.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        pieChartView.setHoleRadius(70f)
        pieChartView.setDescription(null)
    }

    private fun setData(deviceUsersArrayList: ArrayList<DeviceUser>) = with(binding){
        barEntries.clear()
        var totalUser = 0
        var totalAndroid = 0
        var totalIos = 0
        var totalBoth = 0
        var totalRemaining = 0
        for (k in deviceUsersArrayList.indices) {
            totalUser += deviceUsersArrayList[k].totalUser
            totalAndroid += deviceUsersArrayList[k].android
            totalIos += deviceUsersArrayList[k].iOS
            totalBoth += deviceUsersArrayList[k].both
            totalRemaining += deviceUsersArrayList[k].remaining
        }
        tvTotalUser!!.visibility = View.VISIBLE
        tvTotalUser!!.text = getString(R.string.general_total_pun)+"$totalUser"
        tvAndroidCount!!.text = totalAndroid.toString() + ""
        tvIosCount!!.text = totalIos.toString() + ""
        tvBothCount!!.text = totalBoth.toString() + ""
        tvRemainingCount!!.text = totalRemaining.toString() + ""
        tvAndroidPer!!.text = setCalculatedPercentage(totalAndroid, totalUser)
        tvIosPer!!.text = setCalculatedPercentage(totalIos, totalUser)
        tvBothPer!!.text = setCalculatedPercentage(totalBoth, totalUser)
        tvRemainingPer!!.text = setCalculatedPercentage(totalRemaining, totalUser)
        barEntries.add(BarEntry(0f, totalAndroid.toFloat()))
        xAxisLabel.add("Android")
        barEntries.add(BarEntry(1f, totalIos.toFloat()))
        xAxisLabel.add("ios")
        barEntries.add(BarEntry(2f, totalBoth.toFloat()))
        xAxisLabel.add("Both")
        barEntries.add(BarEntry(3f, totalRemaining.toFloat()))
        xAxisLabel.add("Remaining")
        /*for (int i = 0; i < barResponseArray.size(); i++) {
            barEntries.add(new BarEntry(i + 1, barResponseArray.get(i).getResDTL().get(0).getTotal()));
            xAxisLabel.add("Option " + (i + 1) + "");
        }*/
        barChartView.description.isEnabled = false
        barChartView.setMaxVisibleValueCount(600)
        barChartView.setPinchZoom(false)
        barChartView.setDrawBarShadow(false)
        barChartView.setDrawGridBackground(false)
        val xAxis = barChartView.xAxis
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(false)
        xAxis.setAvoidFirstLastClipping(false)
        /*xAxisLabel.add("5");
        xAxisLabel.add("0");
        xAxisLabel.add("3");
        xAxisLabel.add("4");*/xAxis.valueFormatter =
            IAxisValueFormatter { value, axis ->
                if (value >= 0) {
                    if (value <= xAxisLabel.size - 1) xAxisLabel[value.toInt()] else ""
                } else ""
            }

        /*final String[] ds = new String[4];
        ds[0] = "1";
        ds[1] = "2";
        ds[2] = "3";
        ds[3] = "4";


        XAxis xval = barChart.getXAxis();
        xval.setDrawLabels(true);
        xval.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.i("zain", "value " + value);
                return ds[Math.round(value)];
            }


        });*/xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        val rightYAxis = barChartView!!.axisRight
        rightYAxis.isEnabled = false
        barChartView!!.axisLeft.setDrawGridLines(false)


        // add a nice and smooth animation
        barChartView!!.animateY(1500)
        barChartView!!.animateX(1500)
        barChartView!!.legend.isEnabled = false

        /*values.add(new BarEntry(1, 170));
        values.add(new BarEntry(2, 90));
        values.add(new BarEntry(3, 120));
        values.add(new BarEntry(4, 70));*/
        val set1: BarDataSet
        if (barChartView!!.data != null &&
            barChartView!!.data.dataSetCount > 0
        ) {
            set1 = barChartView!!.data.getDataSetByIndex(0) as BarDataSet
            set1.values = barEntriesG1
            barChartView!!.data.notifyDataChanged()
            barChartView!!.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(barEntries, "The year 2017")
            set1.setDrawIcons(false)

//            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            /*int startColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
            set1.setGradientColor(startColor, endColor);*/set1.setColors(
                getResources().getColor(R.color.android),
                getResources().getColor(R.color.ios),
                getResources().getColor(R.color.button_browse_green),
                getResources().getColor(R.color.light_grey)
            )
            // set1.setColors(optionColorArray);
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            //data.setValueTypeface(tfLight);
            data.barWidth = 0.6f
            barChartView.setVisibleYRangeMaximum(1500f, YAxis.AxisDependency.LEFT)
            barChartView!!.setVisibleXRangeMinimum(1f)
            barChartView!!.axisRight.setAxisMinValue(data.yMin)
            barChartView!!.axisRight.setAxisMaxValue(data.yMax)
            barChartView!!.axisRight.setStartAtZero(false)
            barChartView!!.data = data
        }
    }

    private fun writeResponseBodyToDisk(txt: String, receiptNo: String): File? {
        return try {
            val dwldsPath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "/eCarePro Download/AppUser Report/"
            )
            if (!dwldsPath.exists()) {
                dwldsPath.mkdirs()
            }
            val file = File.createTempFile(receiptNo, ".pdf", dwldsPath)
            val pdfAsBytes = Base64.decode(txt, 0)
            val os: FileOutputStream
            os = FileOutputStream(file, false)
            os.write(pdfAsBytes)
            os.flush()
            os.close()
            file
        } catch (e: IOException) {
            null
        }
    }

    fun showNotification(file: File?) {
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val pdfUri: Uri
        pdfUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                requireContext(), "com.franciscan.ecare_pro",
                file!!
            )
        } else {
            Uri.fromFile(file)
        }
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfUri, "application/pdf")
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        val largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ecare_logo)
        val pendingIntent =
            PendingIntent.getActivity(getContext(), 0, pdfIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "channel-01"
        val channelName = "Channel Name"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mNotificationManager?.createNotificationChannel(mChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_push)
            notificationBuilder.setColor(getResources().getColor(R.color.brand_color))
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_push)
        }
        /* TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntent(intent);*/
        notificationBuilder.setContentTitle(
            getResources().getString(
                R.string.app_name
            )
        )
        notificationBuilder.setContentText(getString(R.string.appUserReport_title))
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setPriority(Notification.PRIORITY_MAX)
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setLargeIcon(largeIcon)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setSound(defaultSoundUri)
        mNotificationManager.notify(1, notificationBuilder.build())
    }
}

