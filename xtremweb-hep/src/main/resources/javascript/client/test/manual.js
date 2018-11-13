const createXWHEPClient = require('../xwhep-js-client');

const xwhep = createXWHEPClient({
  login: 'admin',
  password: 'sc16#xw?',
  hostname: 'xw.iex.ec',
  port: '443',
});


const jwtoken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJibG9ja2NoYWluYWRkciI6IjB4YzNmYjI0MzEwNDJmYmRkZTY3YjgzNTZhYmNhYmJjNWMxNDY2MDg0OSIsImlzcyI6Inh3aGVwMTEiLCJpYXQiOjE1MDg0MDA1NjB9.qwkXl9XuL0cWMq8H692RxbHTnyqZauyCYL98ZFgE89k';

xwhep.auth(jwtoken).then((cookies) => {
	xwhep.getApps(cookies).then(console.log)
}).catch((e) => {
    console.log('error ', e);
  });
//xwhep.sendData("<data><uid>72DED894-8972-4441-961E-D58C73FA5778</uid><accessrights>0x755</accessrights><status>UNAVAILABLE</status></data>").then(console.log);
//xwhep.get("0bd208fc-3322-4c38-8a89-8b7dae374f4d").then(console.log);
// xwhep.submit("user", "provider", "creator", "ls", "-la", "stdinContent pouet").then(console.log);
//xwhep.registerApp("", "user", "0xc3fb2431042fbdde67b8356abcabbc5c14660849", "creator", "ls20", "linux", "amd64", "file:///Users/mboleg/DGHEP/IEXEC/github/xwhep-js-client/README.md").then(console.log);
//xwhep.sendData("<data><uid>25d4ec06-aaa7-11e7-8fc0-27efb7f6d3f7</uid><accessrights>0x755</accessrights><name>stdin.txt</name><status>UNAVAILABLE</status></data>").then(xwhep.uploadData("25d4ec06-aaa7-11e7-8fc0-27efb7f6d3f7", "/Users/mboleg/DGHEP/IEXEC/github/xwhep-js-client/README.md").then(console.log));
//xwhep.sendData("<data><uid>25d4ec06-aaa7-11e7-8fc0-27efb7f6d3f7</uid><accessrights>0x755</accessrights><name>stdin.txt</name><status>UNAVAILABLE</status></data>").then(console.log);
//xwhep.uploadData("d38df5de-663a-4831-8aad-9b8afaed226c", "/Users/mboleg/DGHEP/IEXEC/github/xwhep-js-client/88ff7d74-7154-4139-abfb-f32a586f5be9").then(console.log);
//xwhep.getApps().then(console.log);
