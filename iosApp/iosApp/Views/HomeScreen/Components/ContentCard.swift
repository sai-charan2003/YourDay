//
//  ContentCard.swift
//  iosApp
//
//  Created by Sai Charan on 03/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct ContentCard<Content: View>: View {
    
    @State var title : String
    @Binding var isLoading : Bool
    @Binding var hasError : Bool
    let content: () -> Content

    var body: some View {
            GroupBox {
                    HStack {
                        Text(title)
                            .font(.title3)
                            .fontWeight(.medium)
                        Spacer()
                        if(isLoading){
                            ProgressView()
                                .controlSize(.small)
                        }
                        if(hasError){
                            Image(systemName: "exclamationmark.triangle")
                        }
                    }
                    
                    content()  
            }
            .padding(.horizontal)
        }
}

