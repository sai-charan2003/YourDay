//
//  GreetingsComponent.swift
//  iosApp
//
//  Created by Sai Charan on 27/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct GreetingsComponent: View {
    @State var greetings : String
    @State var date : String
    var body: some View {
        VStack(alignment: .leading) {
            Text(greetings)
                .font(.title)
                .bold()
            Text(date)
                .font(.headline)
        }
        .padding(.horizontal)
        .frame(maxWidth: .infinity, alignment: .leading)

        
    }
}

