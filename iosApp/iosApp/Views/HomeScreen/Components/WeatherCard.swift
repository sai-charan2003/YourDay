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
        GroupBox {
            VStack(alignment: .leading) {
                
                if let state = weatherState {
                    if !state.isLocationPermissionGranted {
                        GrantPermissionItem(
                            onGrant: { onLocationPermission() },
                            title: "Please allow location permission to fetch weather data"
                        )
                    } else if state.isLoading {
                        WeatherLoadingItem()
                    } else if let error = state.error {
                        ErrorItem()
                    } else if let weatherData = state.weatherData {
                        WeatherDetailView(weatherData: weatherData)                        
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
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(.horizontal)
    }
}
