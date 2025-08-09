//
//  OnBoardingScreen.swift
//  iosApp
//
//  Created by Sai Charan on 13/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Combine
import EventKit
import PermissionsKit
import CalendarPermission

struct OnBoardingScreen: View {
    let component: Shared.HomeScreenComponent
    @State var homeState : Shared.HomeState?
    @State private var permissionState: PermissionState?
    @State private var selectedTab = 0
    @ObservedObject private var permissionObserver: PermissionObserver = .init()
    init(_ component: HomeScreenComponent) {
        self.component = component
        permissionState = permissionObserver.locationPermission
    }
    
    var body: some View {
        TabView(selection: $selectedTab) {
            ZStack {
                WelcomeScreen(
                    onClick: {
                        selectedTab = 1
                    }
                )
            }
            .tag(0)
            
            PermissionScreen(
                onLocationPermission: {
                    component.onEvent(intent: Shared.HomeEventRequestLocationPermission(showRationale: permissionObserver.locationPermission == .notGranted))
                },
                onCalenderPermission: {
                    component.onEvent(intent: Shared.HomeEventRequestCalendarPermission(showRationale: permissionObserver.calendarPermission == .notGranted))
                },
                onTodoConnect: {
                    component.onEvent(intent: Shared.HomeEventConnectTodoist())
                },
                onCompleted: {
                    self.component.onEvent(intent: Shared.HomeEventOnBoardingFinish())
                    
                }, state: Binding(
                    get: { homeState },
                    set: { _ in }
                )
            )
                .tag(1)
        }
        .onAppear{
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
        
        .tabViewStyle(.page)
        
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
                        print("Calender Permission Is given from onboarding screen")
                        component.onEvent(intent: HomeEventFetchCalendarEvents.shared)
                    } else {
                        print("Calendar access denied")
                    }
                }
            }
        case .authorized:
            print("Calender permission is given")
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
    private func observeState() {
        Task {
            for await state in component.state {
                homeState = state
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
}




struct WelcomeScreen: View {
    @State private var isVisible = false
    let onClick : () -> Void

    var body: some View {
        VStack(alignment: .center) {
            
            Image(resource: MR.images.shared.AppIcon)
                .clipShape(Circle())
                .padding(.vertical)
                .scaleEffect(isVisible ? 1.0 : 0.5)
                .opacity(isVisible ? 1.0 : 0.0)
                .animation(.easeOut(duration: 0.5), value: isVisible)
            Text("Welcome to Your Day")
                .font(.title)
                .fontWeight(.bold)
                .padding(.vertical)
                .offset(y: isVisible ? 0 : 20)
                .opacity(isVisible ? 1.0 : 0.0)
                .animation(.easeOut(duration: 0.6).delay(0.2), value: isVisible)

            Text("Your all-in-one companion for planning your day with weather updates, calendar events, and to-do lists.")
                .font(.headline)
                .fontWeight(.medium)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 20)
                .offset(y: isVisible ? 0 : 20)
                .opacity(isVisible ? 1.0 : 0.0)
                .animation(.easeOut(duration: 0.6).delay(0.3), value: isVisible)
            


            Button(action: {
                
                    onClick()
                
            }) {
                HStack {
                    Text("Let's Go")
                    Image(systemName: "arrow.right")
                }
                .padding()
            }
            .offset(y: isVisible ? 0 : 30)
            .opacity(isVisible ? 1.0 : 0.0)
            .animation(.easeOut(duration: 0.5).delay(0.5), value: isVisible)
            .padding(.bottom, 40)

        }
        .frame(maxWidth: .infinity, maxHeight: .infinity,alignment: .center)
        .onAppear {
            isVisible = true
        }
    }
}

struct PermissionScreen: View {
    let onLocationPermission: () -> Void
    let onCalenderPermission: () -> Void
    let onTodoConnect: () -> Void
    let onCompleted: () -> Void
    @Binding var state: Shared.HomeState?

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            Text("Get the Most Out of Your Day")
                .font(.title2)
                .fontWeight(.bold)
                .multilineTextAlignment(.center)
                .padding(.top, 16)
                .frame(maxWidth: .infinity, alignment: .center)

            FeatureSection(
                icon: "cloud.fill",
                title: "Stay Ahead of the Weather",
                description: "Plan your day with real-time weather updates, so you're always prepared, rain or shine!",
                onClick: onLocationPermission,
                isGranted: Binding(
                    get: { state?.weatherState.isLocationPermissionGranted == true },
                    set: { _ in }
                )
            )
            Divider()

            FeatureSection(
                icon: "calendar",
                title: "Never miss an Event",
                description: "Sync your calendar and keep track of all your important meetings, birthdays, and appointments.",
                onClick: onCalenderPermission,
                isGranted: Binding(
                    get: { state?.calenderData.isCalenderPermissionGranted == true },
                    set: { _ in }
                )
            )
            Divider()

            FeatureSection(
                icon: "checklist",
                title: "Manage Your Tasks Effortlessly",
                description: "Integrate with your favorite to-do apps and see all your daily tasks in one place.",
                isTodoFeature: true, onClick: onTodoConnect,
                isGranted: Binding(
                    get: { state?.todoState.isTodoAuthenticated == true },
                    set: { _ in }
                )
            )


            Button(action: onCompleted) {
                Text("Next")
                    .fontWeight(.bold)
                    .frame(maxWidth: .infinity)
            }
            .padding()
        }
        .padding(.horizontal, 20)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
    }
}

struct FeatureSection: View {
    var icon: String
    var title: String
    var description: String
    var isTodoFeature: Bool = false
    var onClick: () -> Void
    @Binding var isGranted: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(.blue)
                    .font(.title3)
                
                Text(title)
                    .font(.headline)
            }

            Text(description)
                .font(.body)
                .foregroundColor(.gray)

            if !isTodoFeature {
                Button(action: onClick) {
                    HStack {
                        if isGranted {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.green)
                        }
                        Text("Grant Permission")
                    }
                }
                .padding(.top, 5)
            } else {
                HStack {
                    Image(resource: MR.images.shared.Todoist)
                    Text("Connect To Todoist")
                    Spacer()
                    Button(action: onClick) {
                        if isGranted {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.green)
                        }
                        Text("Connect")
                    }
                }
            }
        }
        .padding()
        .frame(maxWidth : .infinity,alignment: .leading)
        
    }
}


