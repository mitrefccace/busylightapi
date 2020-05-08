const electron = require("electron");
const { app, BrowserWindow } = require('electron')
const path = require('path')
const url = require('url')

var bl = require('busylight').get();

bl.on('disconnected', function (err) {
    console.log(err);
    console.log("%%%%%");
});

let mainWindow;

function createWindow() {
    mainWindow = new BrowserWindow({
        webPreferences: {
            nodeIntegration: true
        },
        width: 400,
        height: 480
    })
    mainWindow.loadURL(url.format({
        pathname: path.join(__dirname, 'mainWindow.html'),
        protocol: 'file:',
        slashes: true
    }));
    //open developer tools
    //mainWindow.webContents.openDevTools();

    mainWindow.on('closed', () => {
        mainWindow = null;
        bl.off();
        app.quit();
    });
}


    app.on('ready', createWindow);

    function waitForElement() {
        if (mainWindow !== undefined) {
            //variable exists
            module.exports.mainWindow = mainWindow;
            module.exports.bl = bl;
            const express = require('./server/www');
        }
        else {
            setTimeout(waitForElement, 1000);
        }
    }
    waitForElement();

    app.on('window-all-closed', () => {
        if (process.platform !== 'darwin') {
            bl.off();
            app.quit();
        }
    });

    app.on('activate', () => {
        if (mainWindow === null) {
            createWindow();
        }
    });
