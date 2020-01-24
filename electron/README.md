## Electron Busylight

The Electron Busylight app utilizes the Express node package to read the status of agents and control the Busylight.

### Installation

* `cd electron`
* `npm install`
* Comment out Line 195 in `node_modules/busylight/lib/busylight.js`:

  * On Mac: `sed -i ''  's/this\.keepAliveTimer\.unref/\/\/this\.keepAliveTimer\.unref/' node_modules/busylight/lib/busylight.js`
  * On Windows: `sed -i 's/this\.keepAliveTimer\.unref/\/\/this\.keepAliveTimer\.unref/' node_modules/busylight/lib/busylight.js`
  * Or just edit the file and comment out Line 195

* _Plug the Busylight into your computer_
* `npm start`
* Sign into the ACE Direct agent portal
