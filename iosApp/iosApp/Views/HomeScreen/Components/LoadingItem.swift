//
//  TodoLoadingItem.swift
//  iosApp
//
//  Created by Sai Charan on 11/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct LoadingItem: View {
    @State var text : String
    var body: some View {
        HStack {
            Text(text)
            Spacer()
            ProgressView()
        }
    }
}

