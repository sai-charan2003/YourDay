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
    @Environment(\.scenePhase) private var scenePhase


    @ObservedObject private var permissionObserver: PermissionObserver = .init()
    @State private var permissionState: PermissionState?

    init(_ component: HomeScreenComponent) {
        self.component = component
        permissionState = permissionObserver.locationPermission
    }

    var body: some View {
        NavigationView {
            ScrollView {
                LazyVStack() {
                    VStack(alignment: .leading) {
                        Text(homeState?.greetings ?? "")
                            .font(.title2)
                            .bold()
                        Text(DateUtils().getDateInDDMMYYYY())
                            .bold()
                    }
                    .frame(maxWidth: .infinity,alignment: .leading)
                    .padding()
                    
                    if (homeState?.aiResponseState.isModelDownloaded == true) {
                        if let aiState = homeState?.aiResponseState {
                            AIResponseCard(
                                thinkingResponse: aiState.thinkingResponse ?? "",
                                aiResponse: aiState.aiResponse ?? "",
                                isThinking : aiState.isThinking,
                                showThinking: aiState.showThinkingResponse,
                                onExpandToggle: {
                                    component.onEvent(event: HomeEventOnToggleThinkingResponse())
                                }
                                
                            )
                        }
                    }
                    
                    WeatherCard(
                        weatherState: Binding(
                            get: { homeState?.weatherState },
                            set: { _ in }
                        )
                    ) {
                        component.onEvent(
                            event: HomeEventRequestLocationPermission(
                            
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
                            event: HomeEventRequestCalendarPermission(
                                
                            )
                        )
                    }
                    .padding(.vertical,8)
                    
                    TodoCard(
                        onConnectClick: {
                            component.onEvent(event: HomeEventConnectTodoist.shared)
                        },
                        todoState: Binding(
                            get: { homeState?.todoState },
                            set: { _ in }
                        ),
                        onTodoOpen: { link in
                            component.onEvent(event: Shared.HomeEventOnOpenLink(url: link))
                            
                        }
                    )
                }
            }
        }
            .toolbar{
                ToolbarItem{
                    Menu("more",systemImage: "ellipsis.circle"){
                        Button("Settings") {
                            component.onEvent(event: Shared.HomeEventOpenSettingsPage.shared)
                        }
                        
                    }
                }
            }
            .refreshable {
                component.onEvent(event: HomeEventRefreshData.shared)
            }
            .onAppear {
                observeState()
                checkAndGenerateIfNeeded()
            }

            .onChange(of: homeState?.aiResponseState.isModelDownloaded) { _ in
                checkAndGenerateIfNeeded()
            }
        .onReceive(permissionObserver.$locationPermission) { permissionState in
            switch permissionState {
            case .granted:
                component.onEvent(event: HomeEventFetchWeather.shared)
            default:
                print("Not Granted")
            }
        }
        .onReceive(permissionObserver.$calendarPermission) { permissionState in
            print(permissionState)
            switch permissionState {
            case .granted:
                component.onEvent(event: HomeEventFetchCalendarEvents.shared)
            default:
                print("Not Granted")
            }
        }
        
    }
    

    
    private func checkAndGenerateIfNeeded() {
        guard homeState?.aiResponseState.isModelDownloaded == false else { return }
        component.onEvent(event: HomeEventOnGenerateAIResponse())
    }
    
    private func observeState() {
        Task {
            for await state in component.state {
                homeState = state
            }
        }
    }
}





