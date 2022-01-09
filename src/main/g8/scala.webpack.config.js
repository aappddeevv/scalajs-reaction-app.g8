const webpack = require("webpack")
const { merge } = require("webpack-merge")
const path = require("path")
const CopyWebpackPlugin = require("copy-webpack-plugin")

const distDir = path.join(__dirname, "dist")

function libraryOutput(dest) {
    return {
        output: {
            path: dest,
            filename: "[name].js",
            library: "[name]",
            libraryTarget: "var",
        }
    }
}

// Static content is served from "dist". Webpack dynamically bundled
// content is served from / since there is no publicPath in the 
// devServer definition below or in webpack.output.
const devServer = {
    watchFiles: [distDir],
    static: distDir,
    historyApiFallback: true,
    compress: true,
    hot: true,
    open: true,
    headers: {
        'Access-Control-Allow-Origin': '*'
    }
}

// scalapath: relative path from topdir to scala compiler output .js file
const common = (scalapath) => ({
    // The entry point for this app is a scala entry point but
    // this is not required. You could have your entry point
    // through a js file as you normally would in a js application.
    entry: {
        "Scala": scalapath,
    },
    target: "web",
    resolve: {
        // If using symlinks in node_modules, you need this false so webpack does
        // *not* use the symlink's resolved absolute path as the directory hierarchy.
        symlinks: false,
        extensions: [".ts", ".tsx", ".js"],
        // These aliases are used in scala code and remapped when webpack runs
        // so that you can be location independent inside of .scala files.
        alias: {
            JS: path.resolve(__dirname, "./src/main/js"),
            Public: path.resolve(__dirname, "./src/main/public"),
            App: path.resolve(__dirname, "./src/main/scala/app"),
        },
    },
    module: {
        rules: [
            {
                test: /\.css$/i,
                use: ["style-loader", "css-loader", "postcss-loader"]
            },
            {
                test: /\.tsx$|\.ts$|\.js/,
                exclude: /node_modules/,
                use: "ts-loader"
            },
        ]
    },
    devServer: devServer
})

function copies(dest) {
    return {
        patterns: [{ from: "src/main/public", to: dest }]
    }
}

const dev = {
    devtool: "source-map",
    mode: "development",
}

const prod = {
    devtool: "source-map",
    mode: "production"
}

module.exports = function (env) {
    const isProd = env && env.BUILD_KIND && env.BUILD_KIND === "production"
    // the "app" name must be coordinated with build.sbt
    const scalapath = path.join(__dirname, "./target/scala-3.1.0/app-" + (isProd ? "opt.js" : "fastopt.js"))
    const staticAssets = copies(distDir)
    const output = libraryOutput(distDir)
    const globals = (nodeEnv) => ({
        "process.env": { "NODE_ENV": JSON.stringify(nodeEnv || "development") }
    })
    const copyplugin = new CopyWebpackPlugin(staticAssets)
    //console.log("copyplugin", copyplugin, staticAssets)
    console.log("isProd: ", isProd)
    console.log("scalapath: ", scalapath)
    const modeNone = { mode: "none" }

    if (isProd) {
        const g = globals("production")
        console.log("Production build")
        console.log("globals: ", g)
        return merge(output, common(scalapath), modeNone, prod, {
            plugins: [
                new webpack.DefinePlugin(g),
                copyplugin, // must be first, which means last in the list
            ]
        })
    }
    else {
        const g = globals()
        console.log("Dev build")
        console.log("globals: ", g)
        return merge(output, common(scalapath), modeNone, dev, {
            plugins: [
                new webpack.DefinePlugin(g),
                copyplugin,
            ]
        })
    }
}
