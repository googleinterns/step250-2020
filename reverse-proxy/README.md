A very simple reverse proxy that routes frontend and backend requests to different ports.

### Usage

Install dependencies:

    $ npm install

Run AppEngine dev server (workdir is `/coffee-chats`):

    $ mvn package appengine:run

Run frontend dev server (workdir is `/coffee-chats`)

    $ npm run start

Run the proxy:

    $ node ./index.js

