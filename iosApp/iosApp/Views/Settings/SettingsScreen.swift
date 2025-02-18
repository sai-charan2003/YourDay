//
//  SettingsScreen.swift
//  iosApp
//
//  Created by Sai Charan on 17/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
struct SettingsScreen: View {
    var component : SettingsScreenComponent
    var body: some View {

            List {
                Text("Weather")
                    .onTapGesture {
                        component.onEvent(event: SettingsEvents.OnWeatherItem.shared)
                    }
                Text("Todo Integration")
                    .onTapGesture {
                        component.onEvent(event: Shared.SettingsEvents.OnTodoItem.shared)
                    }
            }
            .toolbarRole(.editor)
            .navigationTitle("Settings")
            

        
    }
}

