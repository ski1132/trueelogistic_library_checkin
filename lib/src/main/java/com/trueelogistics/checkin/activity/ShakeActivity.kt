package com.trueelogistics.checkin.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlinpermissions.KotlinPermissions
import com.trueelogistics.checkin.R
import com.trueelogistics.checkin.fragment.ShakeFindingFragment
import com.trueelogistics.checkin.handler.CheckInTEL
import com.trueelogistics.checkin.interfaces.ArrayListGenericCallback
import com.trueelogistics.checkin.model.HubInDataModel

class ShakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)

        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 0)
        KotlinPermissions.with(this) // where this is an FragmentActivity instance --> KotlinPermissions.with
            .permissions(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).onAccepted {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_shake , ShakeFindingFragment()).commit()
            }.onDenied {
                onBackPressed()
                val intent = Intent(this, CheckInTEL::class.java)
                intent.putExtras(
                    Bundle().apply {
                        putString( CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                            ," Permission of Location Denied !!")
                    }
                )
                CheckInTEL.checkInTEL?.onActivityResult(
                    CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                    Activity.RESULT_OK, intent
                )
            }.ask()

    }

    fun itemShake( activity: Activity ,shakeListener: ShakeCallback ) {
        //order to getLocation
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.let {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(activity) { location: Location? ->
                    CheckInTEL.checkInTEL?.hubGenerater(
                        object : ArrayListGenericCallback<HubInDataModel> {
                            override fun onResponse(dataModel: ArrayList<HubInDataModel>?) {
                                dataModel?.forEach {
                                    val hubLocation = Location(LocationManager.GPS_PROVIDER)
                                    hubLocation.latitude = it.latitude ?: 0.0
                                    hubLocation.longitude = it.longitude ?: 0.0
                                    val distance: Float? = location?.distanceTo(hubLocation)
                                    if (distance != null) {
                                        if (distance < 500)
                                            shakeListener.onFound(it._id, it.locationName)
                                    }
                                }
                            }

                            override fun onFailure(message: String?) {
                                val intent = Intent(activity, CheckInTEL::class.java)
                                intent.putExtras(
                                    Bundle().apply {
                                        putString( CheckInTEL.KEY_ERROR_CHECK_IN_TEL
                                            , " get nameHub onFailure : $message ")
                                    }
                                )
                                CheckInTEL.checkInTEL?.onActivityResult(
                                    CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
                                    Activity.RESULT_OK, intent
                                )
                            }
                        })
                }
        }
    }

    override fun onBackPressed() {
        ShakeFindingFragment.showView = true
        finish()
        CheckInTEL.checkInTEL?.onActivityResult(
            CheckInTEL.KEY_REQUEST_CODE_CHECK_IN_TEL,
            Activity.RESULT_CANCELED, Intent(this, CheckInTEL::class.java)
        )
    }

    interface ShakeCallback {
        fun onFound(hubId: String? = null, hubName: String? = null )
    }
}
