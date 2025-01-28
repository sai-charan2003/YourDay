//
//  Image+Extension.swift
//  iosApp
//
//  Created by Sai Charan on 28/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//
import Shared
import SwiftUI
import UIKit
import Foundation

extension Image {
    init(resource: Shared.ImageResource) {
        if let uiImage = resource.toUIImage() {
            self.init(uiImage: uiImage) // Convert to SwiftUI Image
        } else {
            // Provide a fallback SwiftUI Image if conversion fails
            self.init(systemName: "exclamationmark.triangle")
        }
    }
}


