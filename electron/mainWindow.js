var ipc = require('electron').ipcRenderer;
require('electron').ipcRenderer.setMaxListeners(0);
var busylight = require('busylight').get();

const testbutton = document.getElementById('testbutton');
const busylightbutton = document.getElementById('busylightbutton');
const connectedStatus = document.getElementById('status');
const agentstatus = document.getElementById('agentStatus');
const vendor = document.getElementById('vendorID');

let blink;
let isConnected;
let testButtonCounter = 0;

busylight.on('disconnected', function (err) {
    console.log(err);
    console.log("^^^^^");
  });

function sleep(time) {
    return new Promise((resolve) => setTimeout(resolve, time));
}
vendor.value = busylight.options.vendorId;

/* Recieve data from main process */
ipc.on('info', async function (event, data) {

    if (data.agentstatus !== undefined) {
        agentstatus.value = data.agentstatus;

        if (data.isConnected) {
            testButtonCounter = 0;
            connectedStatus.value = 'Connected';
            blink = data.isBlinking;
            isConnected = data.isConnected;
            testbutton.style.backgroundColor = 'grey';

            testbutton.onclick = function () {
                console.log('Cannot test when connected to server');
            }

            if (blink) {
                while (blink) {
                    await sleep(200).then(() => busylightbutton.style.backgroundColor = data.rgb);
                    await sleep(200).then(() => busylightbutton.style.backgroundColor = 'grey');
                }
            } else {
                busylightbutton.style.backgroundColor = data.rgb;
            }

        } else {
            //enable test button
            testbutton.style.backgroundColor = 'darkslategrey';
            connectedStatus.value = 'Disconnected';
            isConnected = data.isConnected;

            if (testButtonCounter === 0) {
                /* This makes sure the rainbow function doesn't get interrupted */
                busylightbutton.style.backgroundColor = 'grey';
            }
            testButtonCounter = 1;

            testbutton.onclick = async function () {
                event.sender.send('rainbow'); //tell index.js to make busylight rainbow
                rainbow();
            }
        }
    }

});

async function rainbow() {
    if (!isConnected) {
        await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'red' });
        await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'orange' });
        await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'yellow' });
        await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'green' });
        await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'blue' });
        await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'purple' });
        await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'pink' });
        await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'grey' });
    }
}