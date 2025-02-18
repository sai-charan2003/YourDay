//
//  TodoistCard.swift
//  iosApp
//
//  Created by Sai Charan on 11/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct TodoistCard: View {
    var onConnectClick: (() -> Void )
    @Binding var showContent : Bool
    @Binding var isLoading : Bool
    @Binding var todoItems : [TodoistTodayTasksDTO]
    var body: some View {
        GroupBox {
            HStack {
                Image(resource: MR.images.shared.Todoist)
                Text("Todoist Tasks")
                    .font(.title3)
                    .fontWeight(.medium)
            }
            .frame(maxWidth : .infinity, alignment: .leading)
            .padding(.bottom,10)
            
            if(!showContent){
                ConnectToTodoist(onConnectClick: {
                    onConnectClick()
                    
                })
                
            }
            else if(isLoading){
                TodoLoadingItem()
            } else {
                if(todoItems.isEmpty){
                    VStack {
                        Image(resource: MR.images.shared.notasks)
                            .resizable()
                            .frame(width: 50,height: 55)             
                            
                            .padding(.bottom)
                        Text("No tasks today")
                            .font(.subheadline)
                            .fontWeight(.bold)
                        Text("Enjoy your free day")
                            .font(.caption2)
                    }
                    .frame(maxWidth: .infinity, alignment: .center)
                }
                ForEach(todoItems ,id:\.id){ todoItem in
                    Text(todoItem.content)
                        .font(.body)
                        
                }
                
            }
            
        }
        .padding(.horizontal)
    }
}

