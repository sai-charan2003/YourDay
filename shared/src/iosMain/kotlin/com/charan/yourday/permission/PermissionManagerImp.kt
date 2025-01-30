package com.charan.yourday.permission

import platform.EventKit.EKAuthorizationStatusAuthorized
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore

class PermissionManagerImp : PermissionManager {
    var store = EKEventStore()
    override fun hasCalenderPermission(): Boolean {
       when( EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)){
           EKAuthorizationStatusAuthorized-> return true
           else-> return false

        }

    }

    override fun requestCalenderPermission() {
        store.requestFullAccessToEventsWithCompletion { b, nsError ->
        }
    }
}