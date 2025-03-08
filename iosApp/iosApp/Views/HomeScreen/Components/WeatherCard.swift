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
    
    @Binding var weatherState: Shared.WeatherState?
    var onLocationPermission: (() -> Void)

    var body: some View {
        ContentCard(
            title: "Today's Weather",
            isLoading: Binding(get: {weatherState?.isLoading == true}, set: {_ in}),
            hasError: Binding(get: {weatherState?.error != nil}, set: {_ in}),
            content: {
                VStack(alignment: .leading) {
                    if let state = weatherState {
                        if !state.isLocationPermissionGranted {
                            GrantPermissionItem(
                                onGrant: { onLocationPermission() },
                                title: "Please allow location permission to fetch weather data"
                            )
                        }else if let weatherData = state.weatherData {
                            WeatherDetailView(weatherData: weatherData)
                            if let forecast = state.weatherData?.forecast{
                                WeatherForecastItem(forecastData: forecast)
                            }
                        } else {
                            Text("No weather data available")
                                .foregroundColor(.gray)
                                .font(.footnote)
                        }
                    } else {
                        Text("Weather data unavailable")
                            .foregroundColor(.gray)
                            .font(.footnote)
                    }
                    
                }

                
            }
        )
    }
}
