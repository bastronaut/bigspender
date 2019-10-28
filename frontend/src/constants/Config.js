let date = new Date();
let year = date.getFullYear();

const APPCONFIG = {
  brand: 'Henk',
  user: 'Lisa',
  year: year,
  productLink: 'https://wrapbootstrap.com/theme/ant-reactjs-admin-web-app-WB034D136',
  AutoCloseMobileNav: true,           // true, false. Automatically close sidenav on route change (Mobile only)
  noCustomizer: false,
  color: {
    primary: '#00A878',
    success: '#3dbd7d',
    info: '#01BCD4',
    infoAlt: '#948aec',
    warning: '#ffce3d',
    danger: '#f46e65',
    text: '#3D4051',
    gray: '#EDF0F1'
  },
  settings: {
    boxedLayout: false,               // boolean: true, false
    fixedHeader: true,                // boolean: true, false
    collapsedNav: false,              // boolean: true, false
    offCanvasNav: false,              // boolean: true, false
    sidebarWidth: 240,                // number
    colorOption: '34',                // string: 11,12,13,14,15,16; 21,22,23,24,25,26; 31,32,33,34,35,36
    theme: 'light',                   // (WIP) string: light, gray, dark
  }
}

module.exports = APPCONFIG;