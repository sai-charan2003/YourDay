//
//  ConnectToTodoist.swift
//  iosApp
//
//  Created by Sai Charan on 11/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct ConnectToTodoist: View {
    var onConnectClick: (() -> Void )
    var body: some View {
        HStack {
            Text("Connect to todoist")
            Spacer()
            Button("Connect") {
                onConnectClick()
                
            }
        }
    }
}

