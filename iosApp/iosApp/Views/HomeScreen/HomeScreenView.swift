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
    @State private var viewModel = ProvideComponents().getHomeViewModel()
    @State private var uiState: ProcessState<WeatherDTO> = .loading
    @State private var isFetching: Bool = true
    @State private var weatherData: Shared.ProcessState<Shared.WeatherDTO>?
    @State private var calenderEvents : [Shared.CalenderItems]?
    @ObservedObject private var permissionObserver: PermissionObserver = .init()
    @State private var permissionState : PermissionState?
    init () {
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
                        viewModel.grantLocationPermission(shouldShowRationale: permissionObserver.locationPermission != Shared.PermissionState.notGranted)
                    }
                    if let calenderEvents = calenderEvents {
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
                            permissionObserver.checkCalendarPermission()
                            if(permissionObserver.calendarPermission == PermissionState.notGranted){
                                viewModel.grantCalenderPermission(shouldShowRationale: false)
                            }
                        }
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
                        viewModel.getLocation()
                        
                    default :
                        print("Not Granted")
                    
                        
                    }
                    
                    
                }
                .onReceive(permissionObserver.$calendarPermission){ permissionState in
                    
                    print(permissionState)
                    switch permissionState{
                    case Shared.PermissionState.granted:
                        viewModel.getCalenderEvents()
                        
                    default :
                        print("Not Granted calender")
                    
                        
                    }
                    
                    
                }
                
        }
    }

    private func observeWeatherData() {
        viewModel.weatherData.watch { (processState: Shared.ProcessState<WeatherDTO>?) in
            if let processState = processState {
                weatherData = processState
            }

        }
    }
    
    private func observeCalenderData() {
        viewModel.calenderEvents.watch { items in
            if let calenderEvent = items {
                calenderEvents = calenderEvent as! [CalenderItems]?
            }
            
        }
    }
    
}




