//
//  WeatherCard.swift
//  iosApp
//
//  Created by Sai Charan on 28/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct WeatherCardView: View {
    
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
                        LoadingItem()
                    } else if let error = state.error {
                        Text("Error: \(error)")
                            .foregroundColor(.red)
                            .font(.footnote)
                    } else if let weatherData = state.weatherData {
                        HStack {
                            Text(weatherData.location ?? "Unknown Location")
                                .font(.headline)
                            
                            Spacer()
                            
                            if let iconName = weatherData.temperatureIcon {
                                Image(resource: iconName)
                                    .resizable()
                                    .frame(width: 24, height: 24)
                            }
                        }
                        
                        Text("\(weatherData.currentTemperature?.description ?? "--")°")
                            .font(.title)
                            .fontWeight(.bold)
                        
                        HStack {
                            Text("Min: \(weatherData.minTemperature?.description ?? "--")°")
                            Spacer()
                            Text("Max: \(weatherData.maxTemperature?.description ?? "--")°")
                        }
                        .font(.footnote)
                        
                        Text(weatherData.currentCondition ?? "Unknown Condition")
                            .font(.headline)
                            .foregroundColor(.green)
                        
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
