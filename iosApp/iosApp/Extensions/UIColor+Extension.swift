//
//  UIColor+Extension.swift
//  iosApp
//
//  Created by Sai Charan on 30/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI

extension UIColor {
    // Convert UIColor string back to Color for SwiftUI
    static func fromString(_ colorString: String) -> Color {
        // The string will look something like "UIExtendedSRGBColorSpace 0.5 0.2 0.3 1"
        let components = colorString
            .replacingOccurrences(of: "UIExtendedSRGBColorSpace ", with: "")
            .components(separatedBy: " ")
            .compactMap { Double($0) }
        
        if components.count >= 4 {
            return Color(
                red: components[0],
                green: components[1],
                blue: components[2],
                opacity: components[3]
            )
        }
        
        // Return a default color if parsing fails
        return Color.gray
    }
}
