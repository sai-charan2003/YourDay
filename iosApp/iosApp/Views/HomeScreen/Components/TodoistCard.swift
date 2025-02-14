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
            if(!showContent){
                ConnectToTodoist(onConnectClick: {
                    onConnectClick()
                    
                })
                
            }
            else if(isLoading){
                TodoLoadingItem()
            } else {
                ForEach(todoItems ,id:\.self){ todoItem in
                    Text(todoItem.content)
                }
                
            }
            
        }
    }
}

