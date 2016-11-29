import RadioPlayer from './index'
module.exports = async ({radioPath, action}) => {
  switch(action) {
      case "STARTFOREGROUND_ACTION":
      case "PLAY_ACTION":
        RadioPlayer.toggle(radioPath);
        break;
      case "STOPFOREGROUND_ACTION":
        RadioPlayer.stop();
        break;
  }
}