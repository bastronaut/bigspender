import React from 'react';
import { connect } from 'react-redux';
import classnames from 'classnames';
import HeaderLeft from './HeaderLeft';
import HeaderRight from './HeaderRight';


class Header extends React.Component {
  render() {
    const { colorOption } = this.props;

    return (
      <div 
        className={classnames('app-header-inner', {
          'bg-color-light': ['11','12','13','14','15','16','21'].indexOf(colorOption) >= 0,
          'bg-color-dark': colorOption === '31',
          'bg-color-primary': ['22','32'].indexOf(colorOption) >= 0,
          'bg-color-success': ['23','33'].indexOf(colorOption) >= 0,
          'bg-color-info': ['24','34'].indexOf(colorOption) >= 0,
          'bg-color-warning': ['25','35'].indexOf(colorOption) >= 0,
          'bg-color-danger': ['26','36'].indexOf(colorOption) >= 0 })}
      >
        <div className="header-left">
          <HeaderLeft />
        </div>

        <div className="header-right">
          <HeaderRight />
        </div>
      </div>
    );
  }
}


const mapStateToProps = (state) => {
  return {
    colorOption: state.settings.colorOption,
  }
}

module.exports = connect(
  mapStateToProps
)(Header);

