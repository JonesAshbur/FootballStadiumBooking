const webpack = require('webpack');
module.exports = {
  configureWebpack: {
    plugins: [
      new webpack.ProvidePlugin({
        $:"jquery",
        jQuery:"jquery",
        "windows.jQuery":"jquery"
      })
    ]
  },
  devServer: {
    port: 8081,
    host: '0.0.0.0',
    proxy: {
      "/api": {
        target: process.env.VUE_APP_SERVER,
        changeOrigin: true,
        ws: true,
        pathRewrite: { "^/api": "" },
      },
    },
  }
};
