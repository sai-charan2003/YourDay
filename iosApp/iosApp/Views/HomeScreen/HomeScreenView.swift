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

    init(_ component: HomeScreenComponent) {
        self.component = component
    }

    var body: some View {
        NavigationView {
            ScrollView {
                LazyVStack() {
                    VStack(alignment: .leading) {
                        Text(DateUtils().getGreeting())
                            .font(.title2)
                            .bold()
                        Text(DateUtils().getDateInDDMMYYYY())
                            .bold()
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    
                    Observing(component.state) { state in
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
            component.onEvent(event: HomeEventRefreshData.shared)
        }
    }
}
