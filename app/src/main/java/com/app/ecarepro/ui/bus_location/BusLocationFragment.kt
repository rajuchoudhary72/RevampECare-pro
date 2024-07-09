package com.app.ecarepro.ui.bus_location

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.databinding.FragmentBusLocationBinding
import com.app.ecarepro.ui.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BusLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding : FragmentBusLocationBinding
    private val busLocationViewModel : BusLocationViewModel by viewModels()
    private var busNumber =""
    private var busSpeed =0
    private var mMap: GoogleMap? = null

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding= FragmentBusLocationBinding.inflate(inflater,container,false)
         binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ctvRefresh.setOnClickListener {
            hitBusNumber( )
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)



    }

    private fun getBusLocation(vehicleNumber: String ){

        lifecycleScope.launch {
            busLocationViewModel.busLocationStateFlowStateFlow.collectLatest {
                when (it) {
                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }
                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)
                    }
                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)
                        if (it.data!=null){

                            if (it.data.data!=null){
                                binding.tvSpeed.text=it.data.data.speed+" Km/Hour"
                                binding.tvUpdatedOn.text=it.data.data.date_time
                                try {
                                    busSpeed=it.data.data.speed.toInt()
                                }catch (_:Exception){

                                }

                                setUpGoogleMapLocation(it.data.data.latitude,it.data.data.longitude,
                                    it.data.data.location_description)

                            }

                        }

                    }


                }
            }
        }
        busLocationViewModel.busLocation(vehicleNumber )


    }


    private fun setUpGoogleMapLocation(
        latitude: String,
        longitude: String,
        locationDescription: String
    ) {

        val latLng = LatLng(
           latitude.toDouble(),
            longitude.toDouble()
        )
        mMap!!.addMarker(
            MarkerOptions().position(latLng)
                .title(locationDescription)
                .icon(
                    bitmapDescriptorFromVector(
                        this@BusLocationFragment,
                        iconType(busSpeed)
                    )
                )
        )

        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

        binding.llBusDetails.visibility = View.VISIBLE

    }

    private fun iconType(speed:Int) : Int {
        return if (speed>0){
            R.drawable.ic_bus_location_green
        }else
            R.drawable.ic_bus_location_red
    }

    override fun onMapReady(p0: GoogleMap) {

        mMap = p0

        hitBusNumber()

        mMap!!.setInfoWindowAdapter(object : InfoWindowAdapter {
             override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

             override fun getInfoContents(arg0: Marker): View? {
                var v: View? = null
                try {

                     v = layoutInflater.inflate(R.layout.map_custom_marker, null)

                     val addressTxt = v!!.findViewById<View>(R.id.tvAddress) as TextView
                    addressTxt.text = arg0.title
                } catch (ev: Exception) {
                    print(ev.message)
                }
                return v
            }
        })




    }

    private fun hitBusNumber() {

        lifecycleScope.launch {
            busLocationViewModel.vehicleNumberStateFlowStateFlow.collectLatest {
                when (it) {

                    is NetworkResult.Loading -> {
                        (requireActivity() as MainActivity).showLoader(true)
                    }

                    is NetworkResult.Error -> {
                        (requireActivity() as MainActivity).showLoader(false)

                    }

                    is NetworkResult.Success -> {
                        (requireActivity() as MainActivity).showLoader(false)

                        if (it.data!=null){
                            if (it.data.errorCode==0){
                                binding.tvBusNumber.text=it.data.message
                                busNumber=it.data.message
                                getBusLocation(it.data.message)
                            }else{
                                binding.ctvNoData.isVisible=true
                            }

                        }

                    }


                }
            }
        }

        busLocationViewModel.getVehicleNumber( )

    }


    private fun bitmapDescriptorFromVector(
        context: BusLocationFragment,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val background = ContextCompat.getDrawable(requireContext(),  iconType(busSpeed))
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorDrawableResourceId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth + 0,
            vectorDrawable.intrinsicHeight + 0
        )
        val bitmap = Bitmap.createBitmap(
            background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}