import React, { Component } from 'react';
import classnames from 'classnames';

// = styles =
// 3rd
import '../styles/antd.less';
import '../styles/bootstrap.scss';
// custom
import '../styles/layout.scss';
import '../styles/theme.scss';
import '../styles/ui.scss';
import '../styles/app.scss';


function App() {
  return (
    <div id="app-inner" className="full-height">
      <div className="preloaderbar hide"><span className="bar"></span></div>
      <div id="app-main"
        className={classnames('full-height', {
          'fixed-header': true,
          'boxed-layout': false,
          'theme-gray': 'gray' == 'gray',
          'theme-dark': 'theme' == 'dark'
        })
        }
      >
      </div>
    </div>
  );
}

export default App;
