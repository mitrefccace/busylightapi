var express = require('express');
var router = express.Router();
var busylight = require('busylight').get();
const Store = require('electron-store');
const store = new Store();
var blink;

/* GET home page. */
router.get('/', function (req, res, next) {
  res.render('index', { title: 'Successfully Connected to Busylight' });
});

/* get busylight values */
router.use('/setbusylight', function (req, res, next) {
  res.render('index', { title: 'Set Busylight' }, function (err,html) {
    if (err) {
      busylight.off();
    }
  });
  let message = (JSON.parse(Object.keys(req.body)[0]));
  blink = message.blink;
  if (blink) {
    busylight.blink(['rgb(' + message.r + ', ' + message.g + ', ' + message.b + ' )'], 200);
  } else {
    busylight.light('rgb(' + message.r + ', ' + message.g + ', ' + message.b + ' )');
  }
 
  store.set('red', message.r);
  store.set('green', message.g);
  store.set('blue', message.b);
  store.set('status', message.status);
  store.set('blink', message.blink);
  store.set('connected', true);
});

//busylight.off();
store.set('red', null);
store.set('green', null);
store.set('blue', null);
store.set('status', null);
store.set('blink', null);

module.exports = router;