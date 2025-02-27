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
    var onConnectClick: (() -> Void)
    @Binding var todoState: Shared.TodoState?

    var body: some View {
        GroupBox {
            VStack(alignment: .leading) {
                HStack {
                    Image(resource: MR.images.shared.Todoist)
                    Text("Todoist Tasks")
                        .font(.title3)
                        .fontWeight(.medium)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.bottom, 10)

                if let state = todoState {
                    if !state.isTodoAuthenticated {
                        ConnectToTodoist(onConnectClick: onConnectClick)
                    } else if state.isLoading {
                        TodoLoadingItem()
                    } else if let error = state.error {
                        Text("Error: \(error)")
                            .foregroundColor(.red)
                            .font(.footnote)
                    } else if let todoItems = state.todoData, !todoItems.isEmpty {
                        ForEach(todoItems, id: \.id) { todoItem in
                            Text(todoItem.tasks ?? "Unnamed Task")
                                .font(.body)
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

struct EmptyTodoView: View {
    var body: some View {
        VStack {
            Image(resource: MR.images.shared.notasks)
                .resizable()
                .frame(width: 50, height: 55)
                .padding(.bottom)

            Text("No tasks today")
                .font(.subheadline)
                .fontWeight(.bold)

            Text("Enjoy your free day")
                .font(.caption2)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}
