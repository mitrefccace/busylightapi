const mainWindow = require('../main.js').mainWindow;
var ipc = require('electron').ipcMain;
require('electron').ipcMain.setMaxListeners(0);
const busylight = require('../main.js').bl;

var express = require('express');
var router = express.Router();
var blink;
let message;
let latestTime = 0;
let currentTime;

busylight.on('disconnected', function (err) {
  console.log(err);
  console.log("^^^^^");
});

function sleep(time) {
  return new Promise((resolve) => setTimeout(resolve, time));
}

/* GET home page. */
router.get('/', function (req, res, next) {
  res.render('index', { title: 'Successfully Connected to Busylight' });
});

/* get busylight values from ACE Direct */
router.use('/setbusylight', function (req, res, next) {
  res.render('index', { title: 'Set Busylight' });
  message = (JSON.parse(Object.keys(req.body)[0]));
  latestTime = Date.now();

  /* Send data to the renderer process*/
  mainWindow.webContents.send('info', {
    rgb: 'rgb(' + message.r + ', ' + message.g + ', ' + message.b + ' )',
    agentstatus: message.status,
    isBlinking: message.blink,
    isConnected: true
  });

  blink = message.blink;

  if (blink) {
    busylight.blink(['rgb(' + message.r + ', ' + message.g + ', ' + message.b + ' )'], 200);
  } else {
    busylight.light('rgb(' + message.r + ', ' + message.g + ', ' + message.b + ' )');
  }

  console.log(message.status);
  console.log(message.r);
  console.log(message.g);
  console.log(message.b);
  console.log(message.blink);

  console.log("*****************************************");
});

setInterval(checkTimeout, 2000);

function checkTimeout() {
  currentTime = Date.now();
  console.log(currentTime);

  if ((currentTime - latestTime) > 5000) {
    if (mainWindow !== null) {
      mainWindow.webContents.send('info', {
        rgb: 'grey',
        agentstatus: 'Unknown',
        isBlinking: null,
        isConnected: false
      });

      console.log('DISCONNECTED');

      try {
        ipc.on('rainbow', async function (event, args) {
          /* test button clicked */
          busylight.blink(['red', 'orange', 'yellow', 'green', 'blue', 'purple', 'pink'], 500);
          await sleep(4000).then(() => { busylight.off() });
        });
      } catch (err) {
        busylight.off();
      }

    }
  }
}


module.exports = router;