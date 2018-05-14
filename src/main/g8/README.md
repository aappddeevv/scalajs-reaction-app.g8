# App
Your awesome react app!

# Background
scalajs-react and javascript/typescript integrated project. Bundling via webpack

## Input sources

* src/main/js: Javascript/typescript files. You can include all types of assets
  here if they are to be picked up by the bundler.
* src/main/public: Static files, e.g. icons, html. These files are copied
  directly to the output distribution directory.
* src/main/scala: scala.js files

## Output bundle

* dist: The output distribution director that consolidates all of the inputs
  either through copying or bundling.

## Running
First install the javascript side of the application: 

```sh
npm install
```

To run the app in the browser:

```sh
npm run start
```
A browser window should open and show a todo application. If the window opens but the 
app does not display, hit the refresh button.


## Development

You can run and compile the application several ways.

* `npm run start`: Start the app and open a web browser page.
* `npm run build`: Build the production app from npm. The output resides in dist.
* `sbt npmBuildFull`: Build the production app from sbt.
* `sbt npmBuildFast`: Build the fast app from sbt.

More commands...
* `npm run scala:full`: Performs `sbt fullOptJS` from npm.
* `npm run scala:fast`: Performs `sbt fastOptJS` from npm.
* `npm run scala:clean`: Performs `sbt clean` from npm.
* `sbt clean/fastOptJS/fullOptJS`: Runs sbt as you normally would.
* `npm run app`: Performs production webpack bundling.
* `npm run app:dev`: Performs dev webpack bundling.
* `npm run app:dev:start`: Stats webpack-dev-server using fast scala.

For development, since sbt watches the js and public assets directory, you can
use `sbt ~npmBuildFast` to watch all scala and non-scala assets. This runs the
scala build then calls webpack to create your output.

The "app" entries in package.json are used by sbt when sbt calls into the
javascript world to perform webpack bundling.

