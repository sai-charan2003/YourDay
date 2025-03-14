//
//  EventItem.swift
//  iosApp
//
//  Created by Sai Charan on 30/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct CalenderEventItem
: View {
    @State var calenderEvent : Shared.CalenderItems
    var body: some View {
        HStack {
            Divider()
                .frame(width: 2)
                .overlay(UIColor.fromString(calenderEvent.calenderColor!))
                .cornerRadius(8)
                .padding(.vertical,6)
            
            VStack(alignment: .leading) {
                Text(calenderEvent.getEventName())
                    .font(.headline)
                HStack {
                    Text("\(calenderEvent.getFormatedStartTime()) - \(calenderEvent.getFormatedEndTime())")
                        .font(.caption)
                }
                
            }
           
        }
        .frame(maxWidth : .infinity,alignment: .leading)
        .padding(.vertical,1)
    }
}
