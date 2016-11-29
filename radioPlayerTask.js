import RadioPlayer from './index'
module.exports = async ({radioPath, action}) => {
  switch(action) {
      case "STARTFOREGROUND_ACTION":
      case "PLAY":
        RadioPlayer.start(radioPath);
        break;
      case "STOPFOREGROUND_ACTION":
      case "PAUSE":
        RadioPlayer.stop();
        break;
  }
}