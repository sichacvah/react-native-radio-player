import { 
    DeviceEventEmitter,
    NativeModules,
    AppRegistry
} from 'react-native'

let PlayerModule = NativeModules.PlayerModule;

class Player {
    constructor() {
        this.isPause = false;
        this.toggle = this.toggle.bind(this)
        this.start = this.start.bind(this)
        this.stop = this.stop.bind(this)
        this.setVolume = this.setVolume.bind(this)
        this.addListener = this.addListener.bind(this)
        this.removeEventListener = this.removeEventListener.bind(this)
    }

    start(url) {
        PlayerModule.start(url)
        this.isPause = false;
    }

    stop() {
        PlayerModule.stop()
        this.isPause = true;
    }

    toggle(url = "http://lin3.ash.fast-serv.com:6026/stream_96") {
        if (this.isPause) {
            this.start(url)
        } else {
            this.stop()
        }
    }

    startPlayerService(url = "http://lin3.ash.fast-serv.com:6026/stream_96") {
        PlayerModule.startPlayerService(url);
    }

    setVolume(volume = 0) {
        PlayerModule.setVolume(volume)
    }

    addListener(type, cb) {
        switch(type) {
            case "start":
                DeviceEventEmitter.addListener("start", cb)
                break
            case "stop":
                DeviceEventEmitter.addListener("stop", cb)
                break
            case "volume_changed":
                DeviceEventEmitter.addListener("volume_changed", cb)
                break
        }
    }

    removeEventListener(type) {
        DeviceEventEmitter.removeAllListeners(type)
    }
}

AppRegistry.registerHeadlessTask('radioPlayerTask', () => require('./radioPlayerTask'));
export default (new Player());