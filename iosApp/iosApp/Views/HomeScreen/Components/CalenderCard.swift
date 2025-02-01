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
    @Binding var calenderData : [Shared.CalenderItems]
    @Binding var isCalenderPermissionGranted : Bool
    var onGrant : () -> Void
    var body: some View {
        GroupBox{
            VStack(alignment: .leading){
                if(!isCalenderPermissionGranted && calenderData.isEmpty){
                    GrantPermission(onGrant: {
                        onGrant()
                    }, title: "Please allow calender permission to fetch calender events")
                } else {
                    Text(
                        "Today's Event"
                    )
                    .font(.title2)
                    .fontWeight(.medium)
                    
                    ForEach(calenderData,id: \.eventId){ event in
                        EventItem(calenderEvent: event)
                    }
                }
                
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(.horizontal)
        .padding(.vertical,8)
        
        
        
    }
}
