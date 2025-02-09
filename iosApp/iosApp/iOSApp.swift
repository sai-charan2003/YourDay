import SwiftUI
import Shared

@main
struct iOSApp: App {
    // Declare the state variable without an initial value.
    @State private var componentHolder: ComponentHolder<RootComponent>

    init() {
        // Initialize Koin first.
        KointInitHelper().doInitKoin()
        
        // Now initialize the componentHolder after Koin has started.
        _componentHolder = State(initialValue: ComponentHolder { context in
            RootComponent(
                authorizationId: nil,
                errorCode: nil,
                componentContext: context
            )
        })
    }

    var body: some Scene {
        WindowGroup {
            RootView(componentHolder.component)
                .onAppear { LifecycleRegistryExtKt.resume(self.componentHolder.lifecycle) }
                .onDisappear { LifecycleRegistryExtKt.stop(self.componentHolder.lifecycle) }
        }
    }
}

