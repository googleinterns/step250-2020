// this proxy will route requests to /api to backend
// and all the other requests to frontend

const backendDevServerPort = 8080;
const frontendDevServerPort = 3000;
const portThatProxyListensOn = 9000;

const http = require("http");
const httpProxy = require('http-proxy');
const proxy = httpProxy.createProxyServer({});

http.createServer((req, res) => {
    const regex = /^\/(api|_ah)/;
    const port = req.url.match(regex) ? backendDevServerPort : frontendDevServerPort;
    proxy.web(req, res, { target: `http://127.0.0.1:${port}` });
}).listen(portThatProxyListensOn);
