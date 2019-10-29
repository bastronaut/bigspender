import { combineReducers } from 'redux';
import { routerReducer } from 'react-router-redux'

const reducers = {
  routing: routerReducer
};

module.exports = combineReducers(reducers);
