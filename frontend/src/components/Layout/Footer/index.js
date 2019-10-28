import React from 'react';
import APPCONFIG from 'constants/Config';

class Footer extends React.Component {
  render() {
    return (
      <div className="app-footer-inner">
        <span className="footer-left">
          <span>Copyright © <a className="brand" target="_blank" href={APPCONFIG.productLink}>{APPCONFIG.brand}</a> {APPCONFIG.year}</span>
        </span>
        <span className="footer-right">
          <span>Built with Love <i className="anticon anticon-heart"></i></span>
        </span>
      </div>
    )
  }
}

module.exports = Footer;
