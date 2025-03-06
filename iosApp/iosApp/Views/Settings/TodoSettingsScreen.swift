//
//  TodoSettingsScreen.swift
//  iosApp
//
//  Created by Sai Charan on 17/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct TodoSettingsScreen: View {
    var component : Shared.SettingsScreenComponent
    @State var todoToken : String?
    
    var body: some View {
        List{
            HStack {
                Text("Todoist Integration")
                Spacer()
                Button(todoToken == nil ? "Connect" : "Disconnect") {
                    component.onEvent(event: Shared.SettingsEvents.TodoConnect.shared)
                }
            }


        }

        .navigationTitle("Todo Integration")
    }
    
}

