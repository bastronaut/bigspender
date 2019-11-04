import React from 'react';
import classnames from 'classnames';
import APPCONFIG from 'constants/Config';
// import Menu from './Menu'

class Sidebar extends React.Component {

  render() {
    return (
      <div className={classnames('app-sidebar')}>
        <section className={classnames('sidebar-header', 'sidebar-bg-' + APPCONFIG.theme)} >
          <a href="#/" className="brand">{APPCONFIG.brand}</a>
        </section>
        <div className="sidebar-content" ref="sidebarContent">
          {/* <Menu /> */}
        </div>
        <div className="sidebar-footer">
          <a target="_blank" href={APPCONFIG.productLink}>
            <span className="nav-text"><span>Help</span> & <span>Support</span></span>
          </a>
        </div>
      </div>
    );
  }
}

export default Sidebar;