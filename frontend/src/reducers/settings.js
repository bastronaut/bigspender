import APPCONFIG from 'constants/Config';

const initialSettings = APPCONFIG.settings;

const settings = (state = initialSettings, action) => {
  // console.log(action)
  return state;

}

module.exports = settings;