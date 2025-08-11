//
//  RootView.swift
//  iosApp
//
//  Created by Sai Charan on 08/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct RootView: View {
    
    private var root: RootComponent
        
        init(_ root: RootComponent) {
            self.root = root
        }
    
    var body: some View {
        
        StackView(
            stackValue: StateValue(root.childStack),
            onBack: root.onBackClicked
                ) { child in
                    switch child {
                    case let main as Shared.RootComponent.ChildHomeScreen:
                        HomeScreenView(main.component)
                    case let main as Shared.RootComponent.ChildSettingsScreen:
                        SettingsScreen(component:main.component)
                    case let main as Shared.RootComponent.ChildLicenseScreen:
                        WeatherSettingsScreen(component: main.component)
                    case let main as Shared.RootComponent.ChildOnBoardingScreen:
                        OnBoardingScreen(main.component)
                        
                        
                    default: EmptyView()
                    }
                }
            }
        
        
    }



