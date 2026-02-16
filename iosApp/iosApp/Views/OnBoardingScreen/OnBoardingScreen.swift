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
import LocationPermission

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
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .center, spacing: 16) {
                    Text(
                        "Welcome to Your Day"
                    )
                    .font(.title)
                    .fontWeight(.bold)
                    .padding(.top)
                    
                    Text(
                        "Your all-in-one companion for planning your day with weather updates, calendar events and to-do lists"
                    )
                    .font(.subheadline)
                    .fontWeight(.light)
                    .multilineTextAlignment(.center)
                    .padding(.bottom)
                    
                    PermissionCard(
                        title: "Weather Insights",
                        description: "Real-time weather updates to plan your day",
                        systemImage: "sun.max.fill",
                        buttonTitle: "Enable Location",
                        buttonImage: "location.fill",
                        action: {
                            component.onEvent(intent: HomeEventRequestLocationPermission(showRationale: false))
                            
                        },
                        isPermissionGranted: homeState?.weatherState.isLocationPermissionGranted == true
                    )
                    PermissionCard(
                        title: "Calendar Sync",
                        description: "Never miss important events and meetings",
                        systemImage: "calendar",
                        buttonTitle: "Grant Calendar access",
                        buttonImage: "calendar.circle",
                        action: {
                            component.onEvent(intent: HomeEventRequestCalendarPermission(showRationale: false))
                        },
                        isPermissionGranted: homeState?.calenderData.isCalenderPermissionGranted == true
                    )
                    PermissionCard(
                        title: "Task Management",
                        description: "Integrate Todoist and see all your daily tasks in one place",
                        systemImage: "checkmark",
                        buttonTitle: "Connect Todoist",
                        buttonImage: "location.fill",
                        action: {
                            component.onEvent(intent: HomeEventConnectTodoist())
                        },
                        isPermissionGranted: homeState?.todoState.isTodoAuthenticated == true
                    )
                }
                .padding(.horizontal)
            }
            
            Button("Get Started", systemImage: "arrow.right"){
                component.onEvent(intent: HomeEventOnBoardingFinish())
            }
            .frame(maxWidth: .infinity)
            .controlSize(.large)
            .buttonStyle(.borderedProminent)
            .buttonBorderShape(.capsule)
            .padding()
        }
        .onAppear(){
            observeState()
        }
        
        
    }
    private func observeState() {
        Task {
            for await state in component.state {
                print(state)
                await MainActor.run {
                    self.homeState = state
                }
            }
        }
    }
}

struct PermissionCard: View {
    let title: String
    let description: String
    let systemImage: String
    let buttonTitle: String
    let buttonImage: String?
    let action: () -> Void
    let isPermissionGranted: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {

            HStack(spacing: 12) {
                Image(systemName: systemImage)
                    .font(.system(size: 22, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(width: 44, height: 44)
                    .background(
                        Circle()
                            .fill(isPermissionGranted ? Color.green : Color.blue.opacity(0.9))
                    )

                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.headline)

                    Text(description)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }

                Spacer()
            }

            if isPermissionGranted {
                HStack {
                    Image(systemName: "checkmark.circle.fill")
                    Text("Permission Granted")
                        .fontWeight(.semibold)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .foregroundStyle(.green)
                .background(
                    RoundedRectangle(cornerRadius: 14)
                        .fill(Color.green.opacity(0.15))
                )
            } else {
                Button(action: action) {
                    HStack {
                        if let buttonImage {
                            Image(systemName: buttonImage)
                        }
                        Text(buttonTitle)
                            .fontWeight(.semibold)
                    }
                    .frame(maxWidth: .infinity)
                
                }
                .controlSize(.regular)
                .buttonBorderShape(.capsule)
                .buttonStyle(.bordered)
            
            }
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 22, style: .continuous)
                .fill(.background)
        )
        .animation(.easeInOut, value: isPermissionGranted)
    }
}
