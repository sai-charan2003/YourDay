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
    @ObservedObject
    private var childStack: ObservableValue<ChildStack<AnyObject, Shared.RootComponent.Child>>
    
    init(_ component: Shared.RootComponent) {
        self.childStack = ObservableValue(component.childStack)
    }
    
    var body: some View {
        let child = self.childStack.value.active.instance
        
        switch child {
        case let main as Shared.RootComponent.ChildHomeScreen:
            HomeScreenView(main.component)
            
        default: EmptyView()
        }
    }
}


