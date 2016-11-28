import { 
    DeviceEventEmitter,
    NativeModules 
} from 'react-native'

let PlayerModule = NativeModules.PlayerModule;

const Player = {
    start(url = "http://lin3.ash.fast-serv.com:6026/stream_96") {
        PlayerModule.start(url)
    },

    stop() {
        PlayerModule.stop()
    },

    setVolume(volume = 0) {
        PlayerModule.setVolume(volume)
    },

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
    },

    removeEventListener(type) {
        DeviceEventEmitter.removeAllListeners(type)
    }
}


export default Player;