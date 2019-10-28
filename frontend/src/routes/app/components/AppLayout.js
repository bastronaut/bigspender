import React from 'react';
import { connect } from 'react-redux';
import classnames from 'classnames';
import { Layout } from 'antd';
import AppHeader from 'components/Layout/Header';
import AppFooter from 'components/Layout/Footer';
import AppSidebar from 'components/Layout/Sidebar';

const { Header, Footer, Sider, Content } = Layout;

class AppLayout extends React.Component {
  render() {
    const { children, location, fixedHeader } = this.props;


    return (
      <Layout id="app-main-layout" className="ant-layout-has-sider">
        <AppSidebar />

        {
          fixedHeader ?
            <Layout>
              <Header className="app-header"><AppHeader /></Header>
              <Layout>
                <Content className="app-content">
                  {children}
                </Content>
                <Footer className="app-footer"> <AppFooter /> </Footer>
              </Layout>
            </Layout>
            :
            <Layout>
              <Header className="app-header"><AppHeader /></Header>
              <Content className="app-content">
                {children}
              </Content>
              <Footer className="app-footer"> <AppFooter /> </Footer>
            </Layout>
        }
      </Layout>
    )
  }
}

const mapStateToProps = (state, ownProps) => {
  return {
    fixedHeader: state.settings.fixedHeader,
  }
}

module.exports = connect(
  mapStateToProps
)(AppLayout);
