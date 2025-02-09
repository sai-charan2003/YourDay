import Shared

// Source: https://github.com/arkivanov/Decompose/blob/master/sample/app-ios/app-ios/DecomposeHelpers/SimpleChildStack.swift
func simpleChildStack<T : AnyObject>(_ child: T) -> Shared.Value<Shared.ChildStack<AnyObject, T>> {
    return mutableValue(
        Shared.ChildStack(
            configuration: "config" as AnyObject,
            instance: child
        )
    )
}
