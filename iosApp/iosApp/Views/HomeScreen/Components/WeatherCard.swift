//
//  WeatherCard.swift
//  iosApp
//
//  Created by Sai Charan on 28/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct WeatherCardView: View {
    @Binding var weatherData: Shared.WeatherDTO?
    @Binding var isFetching: Bool
    @Binding var isPermissionGranted : Bool
    @Binding  var currentTemperature : String?
    @Binding  var maxTemperature : String?
    @Binding  var minTemperature : String?
    var onLocationPermission : (() -> Void)

    var body: some View {
        GroupBox {
            VStack(alignment: .leading) {
                if(!isPermissionGranted){
                    GrantPermissionItem(
                        onGrant: {
                            onLocationPermission()
                        },
                        title: "Please allow location permission to fetch weather data")
                }
                else if(isFetching)
                {
                    LoadingItem()
                } else{
                    HStack {
                        Text( weatherData?.getLocation() ?? "Unknown Location")
                            .font(.headline)
                        
                        Spacer()                   
                        
                        if let iconName = weatherData?.getImageIcon() {
                            Image(resource: iconName)
                                .resizable()
                                .frame(width: 24, height: 24)
                        }
                        
                    }
                    
                    
                    
                    
                    if let currentTemp = currentTemperature {
                        Text("\(String(currentTemp))")
                            .font(.title)
                            .fontWeight(.bold)
                    }
                    
                    if let minTemp = minTemperature,
                       let maxTemp = maxTemperature {
                        HStack {
                            Text("Min: \(String(minTemp))")
                            Spacer()
                            Text("Max: \(String(maxTemp))")
                        }
                        .font(.footnote)
                    }
                    
                    if let condition = weatherData?.getCurrentCondition() {
                        Text(condition)
                            .font(.headline)
                            .foregroundColor(.green)
                    }
                    
                }
            }
            
            .frame(maxWidth: .infinity, alignment: .leading)
            
        }
        .padding(.horizontal)
        
        
    }
}



