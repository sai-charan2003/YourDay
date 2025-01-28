//
//  UIStateEnum.swift
//  iosApp
//
//  Created by Sai Charan on 28/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Shared
enum ProcessState<T> {
    case loading
    case success(T)
    case error(String)

    func isLoading() -> Bool {
        if case .loading = self {
            return true
        }
        return false
    }

    func extractData() -> T? {
        if case let .success(data) = self {
            return data
        }
        return nil
    }

    func getError() -> String? {
        if case let .error(message) = self {
            return message
        }
        return nil
    }
}

