import React, { Component } from 'react';
import { connect } from 'react-redux';
import classnames from 'classnames';

// = styles =
// 3rd
import 'styles/antd.less';
import 'styles/bootstrap.scss';
// custom
import 'styles/layout.scss';
import 'styles/theme.scss';
import 'styles/ui.scss';
import 'styles/app.scss';


class App extends Component {
  componentDidMount() {}

  render() {
    const { boxedLayout, fixedHeader, theme } = this.props;

    return (
        <div id="app-inner" className="full-height">
          <div className="preloaderbar hide"><span className="bar"></span></div>
          <div
            id="app-main"
            className={classnames('full-height', {
              'fixed-header'  : fixedHeader,
              'boxed-layout'  : boxedLayout,
              'theme-gray'  : theme == 'gray',
              'theme-dark'  : theme == 'dark'})
            }
          >
            {this.props.children}
          </div>
        </div>
    )
  }
}

const mapStateToProps = (state, ownProps) => {
  return {
    boxedLayout: state.settings.boxedLayout,
    fixedHeader: state.settings.fixedHeader,
    theme: state.settings.theme,
  }
}

module.exports = connect(
  mapStateToProps
)(App);
