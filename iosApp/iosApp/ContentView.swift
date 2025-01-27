import SwiftUI
import Shared

struct ContentView: View {
    @State private var viewModel = ProvideComponents().getHomeViewModel()
    
   
    var body: some View {
        VStack {
            Button("Click me!") {
                
                
            }

        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
