//
//  WeatherSettingsScreen.swift
//  iosApp
//
//  Created by Sai Charan on 17/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct WeatherSettingsScreen: View {
    var component: Shared.SettingsScreenComponent

    @State private var weatherUnits: String?
    
    var body: some View {
        List {
            Picker("Temperature Units", selection: $weatherUnits) {
                
                ForEach(Shared.WeatherUnitsEnums.allCases, id: \.self) { item in
                    
                    let unitsString = switch item {
                    case .c: WeatherUnits.shared.C
                    case .f: WeatherUnits.shared.F
                    }

                    Text(unitsString).tag(unitsString)
                }
            }
            .pickerStyle(.menu)
            .onChange(of: weatherUnits ?? "") { newValue in
                component.onEvent(event: Shared.SettingsEvents.OnChangeWeatherUnits(weatherUnit:newValue))
            }
        }
        .navigationTitle("Weather Settings")
        .task {
            await observeWeatherUnits()
        }
    }

    private func observeWeatherUnits() async {
        for await newUnits in component.weatherUnits {

            self.weatherUnits = newUnits
        }
    }
}




