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
    @Binding var weatherData: Shared.WeatherDTO?
    @Binding var isFetching: Bool

    var body: some View {
        GroupBox {
            VStack(alignment: .leading) {
                HStack {
                    Text(isFetching ? "Fetching..." : weatherData?.getLocation() ?? "Unknown Location")
                        .font(.headline)

                    Spacer()
                    if(isFetching){
                        ProgressView()
                    } else {
                        if let iconName = weatherData?.getImageIcon() {
                            Image(resource: iconName)
                                .resizable()
                                .frame(width: 24, height: 24)
                        }
                    }
                }
                if(isFetching){
                    Text("Loading weather data...")
                         } else {
                        
                    
                
                if let currentTemp = weatherData?.getCurrentTemperatureInC() {
                    Text("\(String(currentTemp))°C")
                        .font(.title)
                        .fontWeight(.bold)
                }
                
                if let minTemp = weatherData?.getMinTemperatureInC(),
                   let maxTemp = weatherData?.getMaxTemperatureInC() {
                    HStack {
                        Text("Min: \(String(minTemp))°C")
                        Spacer()
                        Text("Max: \(String(maxTemp))°C")
                    }
                    .font(.footnote)
                }

                if let condition = weatherData?.getCurrentCondition() {
                    Text(condition)
                        .font(.headline)
                        .foregroundColor(.green)
                }
            }
                         }
        }
        .padding(.horizontal)
    }
}



