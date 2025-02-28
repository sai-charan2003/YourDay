//
//  TodoItem.swift
//  iosApp
//
//  Created by Sai Charan on 28/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct TodoDetailsItem: View {
    @State var task: String = ""
    var body: some View {
        Text(task)
            .font(.body)
    }
}
