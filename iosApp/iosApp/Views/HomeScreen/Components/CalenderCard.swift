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
        GroupBox {
            VStack(alignment: .leading) {
                if let calenderState = calenderState {
                    if !calenderState.isCalenderPermissionGranted {
                        GrantPermissionItem(
                            onGrant: onGrant,
                            title: "Please allow calendar permission to fetch events"
                        )
                    } else if calenderState.isLoading {
                        LoadingItem(text: "Fetching events")
                    } else if let error = calenderState.error {
                        ErrorItem()
                    } else if let events = calenderState.calenderData, !events.isEmpty {
                        Text("Today's Events")
                            .font(.title2)
                            .fontWeight(.medium)
                        
                        ForEach(events, id: \.eventId) { event in
                            CalenderEventItem(calenderEvent: event)
                        }
                    } else {
                        EmptyCalendarView()
                    }
                }
                
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
    }
}

struct EmptyCalendarView: View {
    var body: some View {
        VStack {
            Image(resource: MR.images.shared.calender)
                .resizable()
                .frame(width: 50, height: 52)
                .padding(.bottom)
            
            Text("Your calendar is clear")
                .font(.subheadline)
                .fontWeight(.bold)
            
            Text("Enjoy your peaceful day ahead")
                .font(.caption2)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}

