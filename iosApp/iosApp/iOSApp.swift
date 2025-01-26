import SwiftUI
import Shared


@main
struct iOSApp: App {
    init(){
        IOSModuleKt.doInitKoin()
        
    }


    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
