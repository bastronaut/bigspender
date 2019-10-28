module.exports = {
  path: 'app',
  getChildRoutes(partialNextState, cb) {
    require.ensure([], (require) => {
      cb(null, [
        require('./routes/test'),
      ])
    })
  },
  getComponent(nextState, cb) {
    require.ensure([], (require) => {
      cb(null, require('./components/AppLayout'))
    })
  }
}
