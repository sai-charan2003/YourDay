//
//  WeatherCard.swift
//  iosApp
//
//  Created by Sai Charan on 28/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct WeatherCard: View {
    
    let weatherState: Shared.WeatherState
    let onLocationPermission: () -> Void

    var body: some View {
        ContentCard(
            title: "Today's Weather",
            isLoading: .constant(weatherState.isLoading),
            hasError: .constant(weatherState.error != nil),
            content: {
                VStack(alignment: .leading) {
                    if !weatherState.isLocationPermissionGranted {
                        GrantPermissionItem(
                            onGrant: onLocationPermission,
                            title: "Please allow location permission to fetch weather data"
                        )
                    } else if let weatherData = weatherState.currentWeather {
                        WeatherDetailView(weatherData: weatherData)
                        WeatherForecastItem(forecastData: weatherState.forecastWeather)
                    } else {
                        Text("No weather data available")
                            .foregroundColor(.gray)
                            .font(.footnote)
                    }
                }
            }
        )
    }
}
