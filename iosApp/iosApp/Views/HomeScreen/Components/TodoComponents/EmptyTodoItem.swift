//
//  EmptyTodoItem.swift
//  iosApp
//
//  Created by Sai Charan on 01/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct EmptyTodoView: View {
    var body: some View {
        VStack {
            Image(resource: MR.images.shared.notasks)
                .resizable()
                .frame(width: 50, height: 55)
                .padding(.bottom)

            Text("No tasks today")
                .font(.subheadline)
                .fontWeight(.bold)

            Text("Enjoy your free day")
                .font(.caption2)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}
