//
//  WeatherDetailsItem.swift
//  iosApp
//
//  Created by Sai Charan on 28/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct WeatherDetailView: View {
    let weatherData: Shared.WeatherData

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
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
            
            Text("\(weatherData.currentTemperature?.description ?? "--")")
                .font(.title)
                .fontWeight(.bold)
            
            
        }
    }
}

