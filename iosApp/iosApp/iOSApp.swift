import SwiftUI
import Shared


@main
struct iOSApp: App {
    init(){
        KointInitHelper().doInitKoin()
        
    }


    var body: some Scene {
        WindowGroup {
            HomeScreenView()
        }
    }
}
