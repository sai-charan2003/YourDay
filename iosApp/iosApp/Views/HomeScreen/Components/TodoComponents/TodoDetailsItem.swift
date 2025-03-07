//
//  TodoItem.swift
//  iosApp
//
//  Created by Sai Charan on 28/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import RichTextKit

struct TodoDetailsItem: View {
    @State var todoData: Shared.TodoData
    @State private var richTextContext = RichTextContext()
    var onTodoOpen: ((_ url : String) -> Void )

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment : .firstTextBaseline) {
                
                if let attributedString = try? AttributedString(markdown: todoData.tasks ?? "") {
                    Text(attributedString)
                        .frame(maxWidth: .infinity, alignment: .leading)
                } else {
                    Text(todoData.tasks ?? "")
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                
                if todoData.isOverDue == true {
                    RoundedChip(title: "OverDue")

                }
            }

            HStack {
                if let logo = todoData.todoProviderLogo {
                    Image(resource: logo)
                        .resizable()
                        .frame(width: 20, height: 20)
                        .clipShape(Circle())
                } else {
                    Image(systemName: "questionmark.circle")
                        .resizable()
                        .frame(width: 20, height: 20)
                        .foregroundColor(.gray)
                }

                Text(todoData.todoProvider ?? "Unknown Provider")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Spacer()

                Text(formattedDate)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 4)
        .onTapGesture {
            if let link = todoData.taskLink {
                onTodoOpen(link)
            }
        }
        Divider()
    }

    var formattedDate: String {
        if let dueTime = todoData.dueTime {
            return DateUtils().convertToMMMDYYYYWithTime(dueTime)
        } else if let dueDate = todoData.dueDate {
            return DateUtils().convertToMMMDYYYY(dueDate)
        } else {
            return ""
        }
    }
}
