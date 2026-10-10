// Configures webpack dev server for hot reload and live reload
config.devServer = config.devServer || {};

config.devServer.hot = true;
config.devServer.liveReload = true;
config.devServer.historyApiFallback = true;
config.devServer.client = {
    ...(config.devServer.client || {}),
    overlay: true
};
