//
//  CalenderCard.swift
//  iosApp
//
//  Created by Sai Charan on 30/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct CalenderCard : View {
    @State var calenderData : [Shared.CalenderItems]
    var body: some View {
        GroupBox{
            VStack(alignment: .leading){
                Text(
                    "Today's Event"
                )
                .font(.title2)
                .fontWeight(.medium)
                
                ForEach(calenderData,id: \.eventId){ event in
                        EventItem(calenderEvent: event)
                    }
                
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(.horizontal)
        .padding(.vertical,8)
        
        
        
    }
}
