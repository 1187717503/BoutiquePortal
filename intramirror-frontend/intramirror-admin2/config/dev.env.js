// var merge = require('webpack-merge')
// var prodEnv = require('./prod.env')
// console.log(process.env.NODE_ENV);
// return
// module.exports = merge(prodEnv, {
//   NODE_ENV: '"development"',
//
// })

const baseConfig = {
  NODE_ENV: '"development"',
  BASE_URL: '',
  ERROR_URL:'',
  ERROR_URL2:''
};
switch (process.env.WEB_ENV){
  case "loca":
    baseConfig.BASE_URL = '"http://localhost:9999"';
    baseConfig.ERROR_URL = '"http://test.vendor.intramirror.com/"';
    baseConfig.ERROR_URL2 = '"http://test.api.intramirror.com/"';
    break;
  case "test":
    baseConfig.BASE_URL = '"http://test.admin.intramirror.com:8085/"';
    baseConfig.ERROR_URL = '"http://test.vendor.intramirror.com/"';
    baseConfig.ERROR_URL2 = '"http://test.api.intramirror.com/"';
    break;
}
module.exports = baseConfig;
