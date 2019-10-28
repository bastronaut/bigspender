import React from 'react';
import { connect } from 'react-redux';
import { Icon } from 'antd';
import { 
  toggleCollapsedNav,
  toggleOffCanvasNav
} from '../../../actions';


class NavLeft extends React.Component {

  onToggleCollapsedNav = () => {
    const { handleToggleCollapsedNav, collapsedNav } = this.props;
    handleToggleCollapsedNav(!collapsedNav);
  }

  onToggleOffCanvasNav = () => {
    const { handleToggleOffCanvasNav, offCanvasNav } = this.props;
    handleToggleOffCanvasNav(!offCanvasNav);
  }

  render() {
    const { collapsedNav, offCanvasNav } = this.props;

    return (
      <ul className="header-list list-unstyled list-inline">
        <li className="list-inline-item hidden-md-down">
          <Icon
            className="header-icon app-sidebar-toggler"
            type={collapsedNav ? 'menu-unfold' : 'menu-fold'}
            onClick={this.onToggleCollapsedNav}
          />
        </li>
        <li className="list-inline-item">
          <Icon
            className="header-icon app-sidebar-toggler"
            type={offCanvasNav ? 'right-square-o' : 'left-square-o'}
            onClick={this.onToggleOffCanvasNav}
          />
        </li>
      </ul>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    collapsedNav: state.settings.collapsedNav,
    offCanvasNav: state.settings.offCanvasNav,
  }
}
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
)(NavLeft);
