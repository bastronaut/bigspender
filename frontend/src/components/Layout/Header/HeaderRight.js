import React from 'react';
import { Menu, Icon } from 'antd';
import APPCONFIG from 'constants/Config';

const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

class NavRight extends React.Component {
  render() {
    return (
      <Menu
        mode="horizontal"
      >
        <SubMenu title={<span><Icon type="user" />{APPCONFIG.user}</span>}>
          <Menu.Item key="profile:2"><a href="#/app/page/about">About</a></Menu.Item>
          <Menu.Item key="profile:1"><a href="#/login">Logout</a></Menu.Item>
        </SubMenu>
      </Menu>
    );
  }
}

module.exports = NavRight;
