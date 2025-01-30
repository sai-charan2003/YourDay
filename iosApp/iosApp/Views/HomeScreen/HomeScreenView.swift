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

struct HomeScreenView: View {
    @State private var viewModel = ProvideComponents().getHomeViewModel()
    @State private var uiState: ProcessState<WeatherDTO> = .loading
    @State private var isFetching: Bool = true
    @State private var weatherData: Shared.ProcessState<Shared.WeatherDTO>?
    @State private var calenderEvents : [Shared.CalenderItems]?

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
                        
                    })
                )
                if let calenderEvents = calenderEvents {
                    CalenderCard(calenderData: calenderEvents)
                }
                
                
            }
            .frame(maxHeight: .infinity, alignment: .top)
            .navigationTitle(DateUtils().getGreeting())
            .onAppear {
                observeWeatherData()
                observeCalenderData()
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
            print("Items")
            print(items)
            if let calenderEvent = items {
                calenderEvents = items as! [CalenderItems]?
            }
            
        }
    }
}




