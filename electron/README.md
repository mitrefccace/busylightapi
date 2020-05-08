## Electron Busylight

The Electron Busylight app utilizes the Express node package to read the status of agents and control the Busylight.

### Installation
- `cd electron`
- `npm install`
- Plug Busylight into your computer
- `npm start` 
- Sign into the ACE Dirct agent portal

### Building the Standalone App
- `cd electron`
- Build for MacOS:
    - `npm run build-darwin`
- Build for Windows:
    - `npm run build-win32`
- Build for Linux:
    - `npm run build-linux`
- Build for all platforms:
    - `npm run build-all`
- Application will be in `busylightapi/electron/dist`

