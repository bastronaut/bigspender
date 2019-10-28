import React from 'react';
import QueueAnim from 'rc-queue-anim';

const Error404 = () => (
  <div className="err-container text-center">
    <div className="err">
      <h1>404</h1>
      <h2>Sorry, Page Not Found</h2>
    </div>

    <div className="err-body">
      <a href="#/"> Go Back to Home Page </a>
    </div>
  </div>
);

const Page = () => (
  <div className="page-err">
    <QueueAnim type="bottom" className="ui-animate">
      <div key="1">
        <Error404 />
      </div>
    </QueueAnim>
  </div>
);

module.exports = Page;
