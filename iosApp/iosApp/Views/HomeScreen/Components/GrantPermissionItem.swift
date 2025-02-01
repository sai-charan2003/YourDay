//
//  GrantPermission.swift
//  iosApp
//
//  Created by Sai Charan on 01/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct GrantPermissionItem : View {
    var onGrant: (() -> Void )
    var title: String
    var body: some View {
        Text(self.title)
            .font(.subheadline)
        Button("Grant Permission") {
            onGrant()
            
        }
    }
}
