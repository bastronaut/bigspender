import React from 'react';
import classnames from 'classnames';
import { connect } from 'react-redux';
import { Layout, Menu, Icon } from 'antd';
import 'jquery-slimscroll/jquery.slimscroll.min';
import APPCONFIG from 'constants/Config';
import { 
  toggleCollapsedNav,
  toggleOffCanvasNav
} from '../../../../../../ant-template/Ant-ReactJS-Admin-Full-Package v1.1.0/ant/src/actions';
import AppMenu from './Menu'


const { Header, Content, Footer, Sider } = Layout;

class AppSider extends React.Component {

  autoToggleOffCanvasNav = () => {
    const { handleToggleOffCanvasNav } = this.props;
    const width = $(window).width();
    const breakpoint = 992;
    // console.log($(window).width())

    if ( width >= 992 ) {
      handleToggleOffCanvasNav(false);
    } else {
      handleToggleOffCanvasNav(true);
    }
  }

  componentDidMount = () => {
    this.autoToggleOffCanvasNav()
    window.addEventListener("resize", this.autoToggleOffCanvasNav);

    //
    const { sidebarContent } = this.refs;
    $(sidebarContent).slimscroll({
      height: 'calc(100vh - 104px)', // 60(Header) + 44(Footer) 
      size: '7px'
    })
  }

  componentWillUnmount() {
    window.removeEventListener("resize", this.autoToggleOffCanvasNav);
  }


  render() {
    const { collapsedNav, offCanvasNav, sidebarWidth, colorOption } = this.props;
    const collapsedWidth = offCanvasNav ? 0 : 64;

    return (
      <Sider
        collapsible
        collapsed={collapsedNav || offCanvasNav}
        collapsedWidth={collapsedWidth}
        trigger={null}
        width={sidebarWidth}
        className={classnames('app-sidebar', {
          'sidebar-bg-light': ['31', '32', '33', '34', '35', '36'].indexOf(colorOption) >= 0,
          'sidebar-bg-dark': ['31', '32', '33', '34', '35', '36'].indexOf(colorOption) < 0 })}
      >
        <section
          className={classnames('sidebar-header', {
            'bg-color-dark': ['11', '31'].indexOf(colorOption) >= 0,
            'bg-color-light': colorOption === '21',
            'bg-color-primary': ['12', '22', '32'].indexOf(colorOption) >= 0,
            'bg-color-success': ['13', '23', '33'].indexOf(colorOption) >= 0,
            'bg-color-info': ['14', '24', '34'].indexOf(colorOption) >= 0,
            'bg-color-warning': ['15', '25', '35'].indexOf(colorOption) >= 0,
            'bg-color-danger': ['16', '26', '36'].indexOf(colorOption) >= 0 })}
        >
          <svg className="logo-img logo-react" viewBox="0 0 3925 3525" version="1.1" xmlns="http://www.w3.org/2000/svg">
            <circle className="react-dot" stroke="none" cx="1960" cy="1760" r="355" />
            <g className="react-curve" strokeWidth="170" fill="none">
              <ellipse cx="2575" cy="545" rx="715" ry="1875" transform="rotate(30)" />
              <ellipse cx="1760" cy="-1960" rx="715" ry="1875" transform="rotate(90)" />
              <ellipse cx="-815" cy="-2505" rx="715" ry="1875" transform="rotate(-210)" />
            </g>
          </svg>
          <a href="#/" className="brand">{APPCONFIG.brand}</a>
        </section>
        <div className="sidebar-content" ref="sidebarContent">
          <AppMenu />
        </div>
        <div className="sidebar-footer">
          <a target="_blank" href={APPCONFIG.productLink}>
            <Icon type="question-circle" />
            <span className="nav-text"><span>Help</span> & <span>Support</span></span>
          </a>
        </div>
      </Sider>
    );
  }
}

const mapStateToProps = state => ({
  collapsedNav: state.settings.collapsedNav,
  offCanvasNav: state.settings.offCanvasNav,
  sidebarWidth: state.settings.sidebarWidth,
  colorOption: state.settings.colorOption
});

const mapDispatchToProps = (dispatch) => {
  return {
    handleToggleCollapsedNav: (isCollapsedNav) => {
      dispatch( toggleCollapsedNav(isCollapsedNav) );
    },
    handleToggleOffCanvasNav: (isOffCanvasNav) => {
      dispatch( toggleOffCanvasNav(isOffCanvasNav) );
    },
  }
}

module.exports = connect(
  mapStateToProps,
  mapDispatchToProps
)(AppSider);
