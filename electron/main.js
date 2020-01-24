const electron = require("electron");
const{app, BrowserWindow} = require('electron')
const path = require('path')
const url = require('url')

const express = require('./server/www'); 

 var bl = require('busylight').get();

 let mainWindow;


function createWindow() {
    mainWindow = new BrowserWindow( {
        webPreferences:{
            nodeIntegration:true
        },
        width: 800,
        height: 600,
        resizable: false
    })
    mainWindow.loadURL(url.format({
        pathname: path.join(__dirname, 'mainWindow.html'),
        protocol: 'file:',
        slashes: true
    }));
  

    //This opens the developer console at startup
    //mainWindow.webContents.openDevTools();


    mainWindow.on('closed', () => {
        mainWindow = null
        bl.off();
    });
     
}
    
app.on('ready', createWindow);
    
    
app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        bl.off();
     app.quit();
     }
});
    
app.on('activate', () => {
    if (win === null) {
    createWindow();
    }
});
