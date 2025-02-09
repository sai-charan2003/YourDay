//
//  ComponentHolder.swift
//  iosApp
//
//  Created by Sai Charan on 09/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Shared

class ComponentHolder<T> {
    let lifecycle: Shared.LifecycleRegistry
    let component: T
    
    init(factory: (ComponentContext) -> T) {
        let lifecycle = Shared.LifecycleRegistryKt.LifecycleRegistry()
        let component = factory(DefaultComponentContext(lifecycle: lifecycle))
        self.lifecycle = lifecycle
        self.component = component
        
        lifecycle.onCreate()
    }
    
    deinit {
        lifecycle.onDestroy()
    }
}
