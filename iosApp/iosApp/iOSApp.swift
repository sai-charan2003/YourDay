import SwiftUI
import Shared

@main
struct iOSApp: App {
    
    @State private var componentHolder: ComponentHolder<RootComponent>
    @State private var authentizationId : String? = nil
    @State private var errorCode : String? = nil

    init() {
        KointInitHelper().doInitKoin()
        
        _componentHolder = State(initialValue: ComponentHolder {context in
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
                .onOpenURL { url in
                    let code = url.absoluteString.split(separator: "code=").last.map(String.init)
                    let errorCode = url.absoluteString.split(separator: "error=").last.map(String.init)                    
                    self.componentHolder = ComponentHolder { context in
                        RootComponent(
                            authorizationId: code,
                            errorCode: errorCode,
                            componentContext: context
                        )
                    }
                }

        }
    }
}

