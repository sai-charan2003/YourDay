//
//  ErrorItem.swift
//  iosApp
//
//  Created by Sai Charan on 28/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct ErrorItem: View {
    var body: some View {
        VStack {
            Image(resource: MR.images.shared.error)
                .resizable()
                .frame(width: 50, height: 55)
                .padding(.bottom)

            Text("Something went wrong")
                .font(.caption2)
        }
        .frame(maxWidth: .infinity, alignment: .center)
        
    }
}
