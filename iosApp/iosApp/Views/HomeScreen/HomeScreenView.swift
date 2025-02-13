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
    @State private var uiState: ProcessState<WeatherDTO> = .loading
    @State private var isFetching: Bool = true
    @State private var weatherData: Shared.ProcessState<Shared.WeatherDTO>?
    @State private var todoItems : Shared.ProcessState<Shared.TodoistTodayTasksDTO>?

    @State private var calenderEvents : [Shared.CalenderItems]?
    @ObservedObject private var permissionObserver: PermissionObserver = .init()
    @State private var permissionState : PermissionState?
    init(_ component: HomeScreenComponent) {
        self.component = component
        permissionState = permissionObserver.locationPermission
    }

    
    
    var body: some View {
        NavigationStack {
                LazyVStack {
                    WeatherCardView(
                        weatherData: Binding(
                            get: {
                                weatherData?.extractData()
                            },
                            set: { _ in }
                        ),
                        isFetching: Binding(get: {
                            weatherData?.isLoading() == true
                        }, set: { _ in
                            
                        }),
                        isPermissionGranted: Binding(get: {
                            permissionObserver.locationPermission == Shared.PermissionState.granted
                        }, set: { _ in
                            
                        })
                    ) {
                        component.grantLocationPermission(shouldShowRationale: permissionObserver.locationPermission != Shared.PermissionState.notGranted)
                    }
                    
                        CalenderCard(
                            calenderData: Binding(
                                get: {calenderEvents},
                                set: {_ in }),
                            isCalenderPermissionGranted: Binding(
                                get: {
                                    permissionObserver.calendarPermission == Shared.PermissionState.granted
                                },
                                set: { _ in
                                    
                                }
                                )
                        ) {
                            getCalendarPermission()
                        }
                    
                    
                    
                }
                .frame(maxHeight: .infinity, alignment: .top)
                .navigationTitle(DateUtils().getGreeting())
                .onAppear {
                    observeWeatherData()
                    observeCalenderData()
                    
                }
                .onReceive(permissionObserver.$locationPermission){ permissionState in
                    switch permissionState{
                    case Shared.PermissionState.granted:
                        print("Getting")
                        component.getLocation()
                        
                    default :
                        print("Not Granted")
                    
                        
                    }
                    
                    
                }
                
        }
    }

    private func observeWeatherData() {
        component.weatherData.watch { (processState: Shared.ProcessState<WeatherDTO>?) in
            if let processState = processState {
                weatherData = processState
            }

        }
    }
    
    private func observeCalenderData() {
        component.calenderEvents.watch { items in
            if let calenderEvent = items {
                calenderEvents = calenderEvent as! [CalenderItems]?
            }
            
        }
    }
    
    private func observeTodoItems() {
        component.todoistTasks.watch { (processState: Shared.ProcessState<[Shared.TodoistTodayTasksDTO]>) in
            if let processState = processState {
                todoItems = processState
                
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
                        component.isCalenderPermissionGranted()
                        component.isPermissionEnabled(permissions: Shared.Permissions.calender)
                        component.getCalenderEvents()
                    }
                } else {
                    print("Calendar access denied")
                    DispatchQueue.main.async {
                        component.openSettings()
                       
                    }
                }
            }
            
        case .authorized:
            print("Calendar access already authorized")
            
        case .denied, .restricted:
            print("Calendar access denied or restricted")
            component.openSettings()
            
        @unknown default:
            print("Unknown authorization status")
        }
    }
    
}




