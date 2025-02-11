//
//  AppDelegate.swift
//  iosApp
//
//  Created by Sai Charan on 09/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import UIKit


class AppDelegate : NSObject, UIApplicationDelegate {
    var launchURL : URL?
    
    func application(_ application: UIApplication,
                         didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
            // If the app was launched with a URL, capture it.
            if let url = launchOptions?[.url] as? URL {
                self.launchURL = url
            }
            return true
        }
        
        // This method captures URLs opened while the app is already running.
        func application(_ app: UIApplication,
                         open url: URL,
                         options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
            self.launchURL = url
            return true
        }
}
