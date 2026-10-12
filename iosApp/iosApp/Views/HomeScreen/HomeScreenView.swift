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
    @State private var errorMessage: String?

    init(_ component: HomeScreenComponent) {
        self.component = component
    }

    var body: some View {
        NavigationView {
            ScrollView {
                LazyVStack() {
                    Observing(component.state) { state in
                        VStack(alignment: .leading) {
                            Text(state.greetings)
                                .font(.title2)
                                .bold()
                            Text(state.currentDateTime)
                                .bold()
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding()

                        WeatherCard(weatherState: state.weatherState) {
                            component.onEvent(
                                event: HomeEventRequestLocationPermission.shared
                            )
                        }
                        CalendarCard(calendarState: state.calendarData, onGrant: {
                            component.onEvent(
                                event: HomeEventRequestCalendarPermission.shared
                            )
                            
                        })
                        .padding(.vertical, 8)
                        
                        TodoCard(onConnectClick: {
                            component.onEvent(event: HomeEventConnectTodoist.shared)
                            
                        }, todoState: state.todoState, onTodoOpen: { link in
                            component.onEvent(event: Shared.HomeEventOnOpenLink(url: link))
                            
                        })
                    }
                }
            }
        }
        .toolbar {
            ToolbarItem {
                Menu("more", systemImage: "ellipsis.circle") {
                    Button("Settings") {
                        component.onEvent(event: Shared.HomeEventOpenSettingsPage.shared)
                    }
                }
            }
        }
        .refreshable {
            do {
                try await component.refresh()
            } catch is CancellationError {
                // Leaving the screen cancels the refresh action.
            } catch {
                errorMessage = error.localizedDescription
            }
        }
        .task {
            for await effect in component.effects {
                if let toast = effect as? HomeEffectShowToast {
                    errorMessage = toast.message
                }
            }
        }
        .alert("YourDay", isPresented: Binding(
            get: { errorMessage != nil },
            set: { if !$0 { errorMessage = nil } }
        )) {
            Button("OK", role: .cancel) { errorMessage = nil }
        } message: {
            Text(errorMessage ?? "")
        }
    }
}
