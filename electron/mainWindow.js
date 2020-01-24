const { BrowserWindow } = require('electron').remote
const path = require('path')
const url = require('url')
const Store = require('electron-store');
const store = new Store();

const testbutton = document.getElementById('testbutton');
const busylightbutton = document.getElementById('busylightbutton');
const availablebutton = document.getElementById('availablebutton');
const unavailablebutton = document.getElementById('unavailablebutton');
document.getElementById('status').value = "Running";
const agentstatus = document.getElementById('agentStatus');
const callbutton = document.getElementById('callbutton');
const endbutton = document.getElementById('endbutton');
const acceptcall = document.getElementById('acceptcall');
const rejectcall = document.getElementById('rejectcall');
const incomingcalltext= document.getElementById('callscreen');


acceptcall.style.visibility = "hidden";
rejectcall.style.visibility = "hidden";

let blink = false;

let status = "";

var redValue;
var greenValue;
var blueValue;
var lightStatus;
var lightBlink;
var isConnected;

var busylight = require('busylight').get();
busylight.on('disconnected', function (err){
    if (err) console.log(err);
});

/* connected to server */
testbutton.style.backgroundColor = 'grey';
availablebutton.style.backgroundColor = 'grey';
unavailablebutton.style.backgroundColor = 'grey';
callbutton.style.backgroundColor = 'grey';
endbutton.style.backgroundColor = 'grey';

async function update() {
    console.log(store.get('red'));
    console.log(store.get('green'));
    console.log(store.get('blue'));
    console.log(store.get('status'));
    console.log(store.get('connected'));
    console.log(" ");

    redValue = store.get('red');
    greenValue = store.get('green');
    blueValue = store.get('blue');
    lightStatus = store.get('status');
    lightBlink = store.get('blink');
    isConnected = store.get('connected');

    agentstatus.value = lightStatus;

    if (lightBlink) {
        while(lightBlink) {
            await sleep(500).then(() => busylightbutton.style.backgroundColor = 'rgb(' +redValue+ ',' +greenValue+ ',' +blueValue+ ')');
            await sleep(500).then(() => busylightbutton.style.backgroundColor = 'grey');
        }
    } else {
        busylightbutton.style.backgroundColor = 'rgb(' +redValue+ ',' +greenValue+ ',' +blueValue+ ')';

    }

}

setInterval(update, 1000);

function sleep(time) {
    return new Promise((resolve) => setTimeout(resolve, time));
}



/*
testbutton.addEventListener('click', async function (event) {
    busylight.blink(['red', 'orange', 'yellow','green', 'blue', 'purple', 'pink'], 150);
    await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'red' });
    await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'orange' });
    await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'yellow' });
    await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'green' });
    await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'blue' });
    await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'purple' });
    await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'pink' });
    await sleep(500).then(() => { busylightbutton.style.backgroundColor = 'grey' });
    busylight.blink(false);

    if (getStatus() === "Available") {
        busylightbutton.style.backgroundColor = 'green';
        busylight.light('#00FF00')
    } else if (getStatus() === "Unavailable") {
        busylightbutton.style.backgroundColor = 'red';
        busylight.light('red')        
    } else if (getStatus() === "In Call") {
        busylightbutton.style.backgroundColor = 'yellow';
        busylight.light('gold')
    }

});

availablebutton.addEventListener('click', function (event) {
    setStatus("Available");
});

unavailablebutton.addEventListener('click', function (event) {
    setStatus("Unavailable");
});

callbutton.addEventListener('click', function (event) {
    if (getStatus() === "Available") {
        acceptcall.style.visibility = "visible";
        rejectcall.innerText = "Reject Call";
        rejectcall.style.visibility = "visible";
        incomingcalltext.value="Incoming Call";

        blink = true;
        incomingCall();

        acceptcall.addEventListener('click', function (event) {
            blink = false;
            setStatus("In Call");

            acceptcall.style.visibility = "hidden";
            rejectcall.style.visibility = "hidden";
            incomingcalltext.value="Call in Session";
        });

        rejectcall.addEventListener('click', function (event) {
            blink = false;
            acceptcall.style.visibility="hidden";
            rejectcall.style.visibility="hidden";
            incomingcalltext.value="";
        });

        endbutton.addEventListener('click', function (event) {
            blink = false;
            acceptcall.style.visibility="hidden";
            rejectcall.style.visibility="hidden";
            incomingcalltext.value="";
        });
    }
});


async function incomingCall() {
    busylight.blink(['blue'], 250);
    while (blink) {
        if (getStatus() === "In Call") {
            console.log(getStatus());
        }       
        
        await sleep(250).then(() => { busylightbutton.style.backgroundColor = 'blue' });
        await sleep(250).then(() => { busylightbutton.style.backgroundColor = 'grey' });

        //await sleep(250).then(() => { busylight.light('grey')});
       // await sleep(250).then(() => { busylight.light('blue') });
        
    };

    if (getStatus() === "Available") {
        setStatus("Available");
    } else if (getStatus() === "Unavailable") {
        setStatus("Unavailable");
    } else {
        setStatus("In Call");
    }

    if (getStatus() === "In Call") {
        // TODO make timer

        rejectcall.style.visibility = "visible";
        rejectcall.innerText = "End Call";

        rejectcall.addEventListener('click', function (event) {
            setStatus("Available");
            rejectcall.style.visibility="hidden";
            incomingcalltext.value="";
        });

        endbutton.addEventListener('click', function (event) {
            rejectcall.style.visibility="hidden";
            incomingcalltext.value="";
            setStatus("Available");
        });

    }

}


function setStatus(s) {
    if (s === "Available") {
        busylightbutton.style.backgroundColor = 'green';
        agentstatus.value = "Available";
        status = "Available";
        busylight.light('#00FF00');
    } else if (s === "Unavailable") {
        busylightbutton.style.backgroundColor = 'red';
        agentstatus.value = "Unavailable";
        status = "Unavailable";
        busylight.light('red');
    } else if (s === "In Call") {
        busylightbutton.style.backgroundColor = 'yellow';
        agentstatus.value = "In Call";
        status = "In Call";
        busylight.light('gold');
    }
}

function getStatus() {
    return status;
}

*/