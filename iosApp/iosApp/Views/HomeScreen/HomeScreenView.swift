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
    @State private var homeState: Shared.HomeState?

    @ObservedObject private var permissionObserver: PermissionObserver = .init()
    @State private var permissionState: PermissionState?

    init(_ component: HomeScreenComponent) {
        self.component = component
        permissionState = permissionObserver.locationPermission
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                VStack(alignment: .leading) {
                    Text(DateUtils().getGreeting())
                        .font(.title3)
                        .bold()
                    Text(DateUtils().getDateInDDMMYYYY())
                        .bold()
                }
                Spacer()
                Menu {
                    Button("Settings") {
                        component.onEvent(
                            intent: Shared.HomeEventOpenSettingsPage.shared
                        )
                    }
                    
                } label: {
                    Image(systemName: "ellipsis.circle")
                    
                }

            }
            .padding(.horizontal)
            .background(Color(UIColor.systemBackground))
        }
            ScrollView {
                LazyVStack() {

                        WeatherCard(
                            weatherState: Binding(
                                get: { homeState?.weatherState },
                                set: { _ in }
                            )
                        ) {
                            component.onEvent(
                                intent: HomeEventRequestLocationPermission(
                                    showRationale: permissionObserver.locationPermission == .notGranted
                                )
                            )
                        }
                        
                        CalenderCard(
                            calenderState: Binding(
                                get: { homeState?.calenderData },
                                set: { _ in }
                            )
                        ) {
                            component.onEvent(
                                intent: HomeEventRequestCalendarPermission(
                                    showRationale: permissionObserver.calendarPermission == .notGranted
                                )
                            )
                        }
                        
                        TodoCard(
                            onConnectClick: {
                                component.onEvent(intent: HomeEventConnectTodoist.shared)
                            },
                            todoState: Binding(
                                get: { homeState?.todoState },
                                set: { _ in }
                            )
                        )
                    }
                }
            
            .navigationBarHidden(true)
            .ignoresSafeArea()
        
        .onAppear {
            observeState()
            observePermissionRequest()
        }
        .onReceive(permissionObserver.$locationPermission) { permissionState in
            switch permissionState {
            case .granted:
                component.onEvent(intent: HomeEventFetchWeather.shared)
            default:
                print("Not Granted")
            }
        }
        .onReceive(permissionObserver.$calendarPermission) { permissionState in
            switch permissionState {
            case .granted:
                component.onEvent(intent: HomeEventFetchCalendarEvents.shared)
            default:
                print("Not Granted")
            }
        }
    }
    
    private func observePermissionRequest() {
        Task {
            for await effect in component.effects {
                switch effect {
                case is Shared.HomeViewEffectRequestCalenderPermission:
                    getCalendarPermission()
                case is Shared.HomeViewEffectRequestLocationPermission:
                    getLocationPermission()
                case let toastEffect as Shared.HomeViewEffectShowToast:
                    // Handle toast if needed
                    break
                default:
                    break
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
                DispatchQueue.main.async {
                    if granted {
                        component.onEvent(intent: HomeEventFetchCalendarEvents.shared)
                    } else {
                        print("Calendar access denied")
                    }
                }
            }
        case .authorized:
            component.onEvent(intent: HomeEventFetchCalendarEvents.shared)
        case .denied, .restricted:
            component.onEvent(intent: HomeEventRequestCalendarPermission(showRationale: true))
        @unknown default:
            print("Unknown authorization status")
        }
    }
    
    private func getLocationPermission() {
        let status = CLLocationManager.authorizationStatus()
        switch status {
        case .notDetermined:
            CLLocationManager().requestWhenInUseAuthorization()
        case .authorizedWhenInUse, .authorizedAlways:
            component.onEvent(intent: HomeEventFetchWeather.shared)
        case .denied, .restricted:
            component.onEvent(intent: HomeEventRequestCalendarPermission(showRationale: false))
        @unknown default:
            print("Unknown authorization status")
        }
    }
}





