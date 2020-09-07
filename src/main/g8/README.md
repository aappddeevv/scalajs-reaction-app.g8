# App
Your awesome react app!

# Background
scalajs-reaction and javascript/typescript integrated project. Bundling via webpack.

The main scalajs-react project is located at [github](https://github.com/aappddeevv/scalajs-reaction).

This scalajs-react is different than the jpgolly/scalajs-react and a few others that have the same name. This React facade uses the [ReasonReact API](https://reasonml.github.io/reason-react). This API is now based exclusively on function components and hooks.

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
* `npm run app:dev:start`: Starts webpack-dev-server using fast scala.
* `npm run app:dev:watch`: Starts webpack-dev-server and watches for js file changes. 

For development, since sbt watches the js and public assets directory, you can
use `sbt ~npmBuildFast` to watch all scala and non-scala assets and perform a
webpack build when anything changes. This is a scala-first approach.

It easier and faster to run sbt as you normally do, for example from within
emacs and have a shell window open running `npm run app:dev:start`. This
approach starts a browser instance or opens a new tab in an existing browser
then waits for an updated scalajs output file and re-injects it into the browser
automatically. It also watches any typescript/js files and recompiles them when
they change. This is the webpack-first approach.

The "app" entries in package.json are used by sbt when sbt calls into the
javascript world to perform webpack bundling.

## JS Parts

The package.json and webpack config file is a bit messy and some of its content
is unnecessary. It will be cleaned up in future releases of this template.

## Versions

This template currently uses 2.13 but not all scala.js packages are available
for 2.13 yet. scalajs-reaction uses a baseline of 2.13 so beware of
dependencies. If we need 2.12 versions, that can be published based on demand.
