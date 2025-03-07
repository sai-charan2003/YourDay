//
//  RoundedChip.swift
//  iosApp
//
//  Created by Sai Charan on 06/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct RoundedChip: View {
    @State var title : String
    var body: some View {
        Text("Overdue")
            .font(.caption)
            .fontWeight(.bold)
            .foregroundColor(.white)
            .padding(.horizontal, 4)
            .background(Color.red.opacity(0.8))
            .clipShape(RoundedRectangle(cornerRadius: 10))
    }
}
