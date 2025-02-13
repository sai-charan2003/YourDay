//
//  TodoLoadingItem.swift
//  iosApp
//
//  Created by Sai Charan on 11/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct TodoLoadingItem: View {
    var body: some View {
        Text("Fetching tasks")
        Spacer()
        ProgressView()
    }
}

#Preview {
    TodoLoadingItem()
}
