//
//  TodoistCard.swift
//  iosApp
//
//  Created by Sai Charan on 11/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct TodoCard: View {
    var onConnectClick: (() -> Void)
    @Binding var todoState: Shared.TodoState?
    var onTodoOpen: ((_ url : String) -> Void )

    var body: some View {
        GroupBox {
            VStack(alignment: .leading) {
                if let state = todoState {
                    if !state.isTodoAuthenticated {
                        TodoConnectItem(onConnectClick: onConnectClick)
                    } else if state.isLoading {
                        LoadingItem(text: "Fetching Tasks")
                    } else if let error = state.error {
                        ErrorItem()
                    }
                    else if let todoItems = state.todoData, !todoItems.isEmpty {
                            Text("Today's tasks")
                                .font(.title3)
                                .fontWeight(.medium)
                        
                        
                        ForEach(todoItems, id: \.id) { todoItem in
                            TodoDetailsItem(todoData: todoItem){ link in
                                onTodoOpen(link)
                                
                            }
                        }
                    } else {
                        EmptyTodoView()
                    }
                } else {
                    Text("No Todoist data available")
                        .foregroundColor(.gray)
                        .font(.footnote)
                }
            }
        }
        .padding(.horizontal)
    }
}


