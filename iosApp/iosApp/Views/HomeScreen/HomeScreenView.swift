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

    var body: some View {
        NavigationStack {
            VStack {
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
                .frame(maxHeight: .infinity, alignment: .top)
            }
            .navigationTitle(DateUtils().getGreeting())
            .onAppear {
                observeWeatherData()
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
}




