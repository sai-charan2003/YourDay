//
//  MutableValue.swift
//  iosApp
//
//  Created by Sai Charan on 09/02/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Shared

func mutableValue<T>(_ value: T) -> Shared.MutableValue<T> {
    return Shared.MutableValueBuilderKt.MutableValue(initialValue: value) as! MutableValue<T>
    
}
