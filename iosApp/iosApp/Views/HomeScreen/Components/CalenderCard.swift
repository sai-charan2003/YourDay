//
//  CalenderCard.swift
//  iosApp
//
//  Created by Sai Charan on 30/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct CalenderCard: View {
    @Binding var calenderState: Shared.CalenderState?
    var onGrant: () -> Void

    var body: some View {
        ContentCard(
            title: "Today's Events",
            isLoading: Binding(get: {calenderState?.isLoading == true}, set: {_ in}),
            hasError: Binding(get: {calenderState?.error != nil}, set: {_ in}),
            content: {
                VStack(alignment: .leading) {
                    if let calenderState = calenderState {
                        if !calenderState.isCalenderPermissionGranted {
                            GrantPermissionItem(
                                onGrant: onGrant,
                                title: "Please allow calendar permission to fetch events"
                            )
                        } else if let events = calenderState.calenderData, !events.isEmpty {
                            
                            ForEach(events, id: \.eventId) { event in
                                CalenderEventItem(calenderEvent: event)
                            }
                        } else if calenderState.calenderData != nil {
                            EmptyCalendarView()
                        }
                    }
                    
                }

                
            }
        )
        
    }
}


