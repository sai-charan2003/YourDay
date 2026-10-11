import SwiftUI
import Shared

struct SettingsScreen: View {
    let component: SettingsScreenComponent

    var body: some View {
        Observing(component.settingsState) { state in
            List {
                Section("Preferences") {
                    Picker(selection: Binding(
                        get: { state.weatherUnits ?? WeatherUnits.shared.C },
                        set: { newValue in
                            component.onEvent(event: SettingsEvents.OnChangeWeatherUnits(weatherUnit: newValue))
                        }
                    )) {
                        Text("Celsius (°C)").tag(WeatherUnits.shared.C)
                        Text("Fahrenheit (°F)").tag(WeatherUnits.shared.F)
                    } label: {
                        Label("Temperature units", systemImage: "thermometer.medium")
                    }
                    .pickerStyle(.menu)
                    .disabled(state.weatherUnits == nil)
                }

                Section("Integrations") {
                    let isConnected = state.isTodoistConnected?.boolValue ?? false

                    HStack {
                        Label("Todoist", systemImage: "checkmark.circle")
                        Spacer()
                        if state.isTodoistConnected == nil {
                            ProgressView()
                                .accessibilityLabel("Loading Todoist connection")
                        } else {
                            Button(isConnected ? "Disconnect" : "Connect", role: isConnected ? .destructive : nil) {
                                component.onEvent(event: SettingsEvents.TodoConnect.shared)
                            }
                            .buttonStyle(.borderless)
                        }
                    }
                }

                Section("About") {
                    HStack {
                        Label("App version", systemImage: "info.circle")
                        Spacer()
                        Text(state.appVersion ?? "—")
                            .foregroundStyle(.secondary)
                    }
                }
            }
            .listStyle(.insetGrouped)
        }
        .toolbarRole(.editor)
        .navigationTitle("Settings")
    }
}
