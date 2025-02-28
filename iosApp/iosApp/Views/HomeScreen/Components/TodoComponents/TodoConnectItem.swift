//
//  ConnectToTodoist.swift
//  iosApp
//
//  Created by Sai Charan on 11/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct TodoConnectItem: View {
    var onConnectClick: (() -> Void )
    var body: some View {
        HStack {
            Image(resource: MR.images.shared.Todoist)
            Text("Connect to todoist")
            Spacer()
            Button("Connect") {
                onConnectClick()
                
            }
        }
    }
}

