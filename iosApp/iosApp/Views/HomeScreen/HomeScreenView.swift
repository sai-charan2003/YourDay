//
//  HomeScreenView.swift
//  iosApp
//
//  Created by Sai Charan on 27/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import Shared
import Combine
import EventKit

struct HomeScreenView: View {
    private let component: HomeScreenComponent
    @State private var homeState : Shared.HomeState?

    @ObservedObject private var permissionObserver: PermissionObserver = .init()
    @State private var permissionState : PermissionState?
    init(_ component: HomeScreenComponent) {
        self.component = component
        permissionState = permissionObserver.locationPermission
    }

    
    
    var body: some View {

                LazyVStack {
                    WeatherCardView(
                        weatherState: Binding(
                            get: {
                                homeState?.weatherState
                            },
                            set: { _ in }
                        )
                    ) {
                        component.onEvent(intent:HomeEventRequestLocationPermission(showRationale: permissionObserver.locationPermission == Shared.PermissionState.notGranted))
                    }
                    
                        CalenderCard(
                            calenderState: Binding(
                                get: {homeState?.calenderData},
                                set: {_ in })
                        ) {
                            component.onEvent(intent:HomeEventRequestCalendarPermission(showRationale: permissionObserver.calendarPermission == Shared.PermissionState.notGranted))
                        }
                    TodoistCard(
                        onConnectClick: {
                            component.onEvent(intent: HomeEventConnectTodoist.shared)
                        },
                        todoState: Binding(get: {homeState?.todoState }, set: {_ in })
                    )
                    
                    
                    
                    
                }
                .frame(maxHeight: .infinity, alignment: .top)
                .navigationTitle(DateUtils().getGreeting())
                .toolbarRole(.editor)
                .onAppear{
                    observeState()
                    observePermissionRequest()
                }
                .toolbar{
                    ToolbarItem{
                        Menu("more",systemImage: "ellipsis.circle"){
                            Button("Settings") {
                                component.onEvent(intent: Shared.HomeEventOpenSettingsPage.shared)
                            }
                        }
                    }
                }
                .onReceive(permissionObserver.$locationPermission){ permissionState in
                    switch permissionState{
                    case Shared.PermissionState.granted:
                        print("Getting")
                        component.onEvent(intent: HomeEventFetchWeather.shared)
                        
                    default :
                        print("Not Granted")
                    
                        
                    }
                    
                    
                }
                .onReceive(permissionObserver.$calendarPermission){ permissionState in
                    switch permissionState{
                    case Shared.PermissionState.granted:
                        print("Getting")
                        component.onEvent(intent: HomeEventFetchCalendarEvents.shared)
                        
                    default :
                        print("Not Granted")
                    }
                    
                }
                
        
    }
    
    private func observePermissionRequest(){
        Task {
            for await state in component.state{
                let request = state.requestCalenderPermission
                if request{
                    getCalendarPermission()
                    self.component.onEvent(intent: HomeEventResetCalenderPermission.shared)
                    
                }
                let locationPermission = state.requestLocationPermission
                if locationPermission{
                    getLocationPermission()
                    self.component.onEvent(intent: HomeEventResetLocationPermission.shared)
                }
                
            }
        }
    }
    
    private func observeState() {
        Task {
            for await state in component.state {
                homeState = state
            }
        }
    }


    
    private func getCalendarPermission() {
        let eventStore = EKEventStore()
        let status = EKEventStore.authorizationStatus(for: .event)
        
        switch status {
        case .notDetermined:
            eventStore.requestAccess(to: .event) { granted, error in
                if let error = error {
                    print("Error requesting access: \(error.localizedDescription)")
                    return
                }
                if granted {
                    print("Calendar access granted")
                    DispatchQueue.main.async {
                        component.onEvent(intent: HomeEventFetchCalendarEvents.shared)
                    }
                } else {
                    print("Calendar access denied")
                    DispatchQueue.main.async {
                        component.onEvent(intent: HomeEventRequestCalendarPermission(showRationale: false))
                       
                    }
                }
            }
            
        case .authorized:
            print("Calendar access already authorized")
            DispatchQueue.main.async {
                component.onEvent(intent: HomeEventFetchCalendarEvents.shared)
               
            }
            
            
        case .denied, .restricted:
            print("Calendar access denied or restricted")
            component.onEvent(intent: HomeEventRequestCalendarPermission(showRationale: false))
            
        @unknown default:
            print("Unknown authorization status")
        }
    }
    
    private func getLocationPermission() {
        let status = CLLocationManager.authorizationStatus()
        switch status {
        case .notDetermined:
            CLLocationManager().requestWhenInUseAuthorization()
        case .authorizedWhenInUse,.authorizedAlways:
            component.onEvent(intent: HomeEventFetchWeather.shared)
        case .denied,.restricted:
            component.onEvent(intent: HomeEventRequestCalendarPermission(showRationale: false))
        @unknown default:
            print("Unknown authorization status")
            
        }
    }
    
}




