package com.charan.yourday.permission

import com.charan.yourday.utils.asCommonFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorized
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.EventKit.EKAuthorizationStatus
import platform.EventKit.EKAuthorizationStatusAuthorized
import platform.EventKit.EKAuthorizationStatusFullAccess
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

class PermissionManagerImp : PermissionManager {
    private var store = EKEventStore()
    private var location = CLLocationManager()
    private var calenderPermission = MutableStateFlow(false)

    override fun isPermissionGranted(permissions: Permissions): Boolean {
        return when (permissions) {
            Permissions.CALENDER -> {
                val status = EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)
                val isGranted = status == EKAuthorizationStatusAuthorized || status == EKAuthorizationStatusFullAccess
                isGranted
            }

            Permissions.LOCATION -> {
                location.locationServicesEnabled()
            }
        }
    }

    override fun requestPermission(permissions: Permissions) {
        when(permissions){

            Permissions.CALENDER -> {
                store.requestFullAccessToEventsWithCompletion { isgranted, nsError ->
                    if(isgranted){
                        calenderPermission.tryEmit(true)

                    } else{
                        calenderPermission.tryEmit(false)
                    }
                }
            }
            Permissions.LOCATION -> {
                val status = CLLocationManager.authorizationStatus()
                when(status){
                    kCLAuthorizationStatusAuthorized -> {
                        print("granted")
                    }
                    kCLAuthorizationStatusNotDetermined ->{
                        location.requestWhenInUseAuthorization()
                    }
                }


            }
        }
    }

    override fun requestMultiplePermissions(permissions: List<Permissions>) {
        permissions.forEach {
            requestPermission(it)
        }
    }

    override fun openAppSettings() {
        val settingsUrl: NSURL = NSURL.URLWithString(UIApplicationOpenSettingsURLString)!!
        UIApplication.sharedApplication.openURL(settingsUrl, mapOf<Any?, Any>(), null)

    }

    override fun observeCalenderPermission() = calenderPermission.asCommonFlow()
}
