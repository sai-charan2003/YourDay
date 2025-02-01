//
//  LoadingItem.swift
//  iosApp
//
//  Created by Sai Charan on 01/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct LoadingItem: View {
    var body: some View {
        VStack{
            HStack {
                Text("Fetching...")
                    .font(.headline)
                Spacer()
                ProgressView()
            }
            Text("Loading weather data")
        }
    }
}

#Preview {
    LoadingItem()
}
