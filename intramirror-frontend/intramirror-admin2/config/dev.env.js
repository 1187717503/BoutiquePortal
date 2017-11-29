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
  API_URL: '',
  ERROR_URL: '',
  ERROR_URL2: ''
};
switch (process.env.WEB_ENV) {
  case "loca":
    baseConfig.BASE_URL = '"http://localhost:8080"';
    baseConfig.ERROR_URL = '"http://test.vendor.intramirror.com:8082/"';
    baseConfig.ERROR_URL2 = '"http://test.api.intramirror.com:8088/"';
    break;
  case "test":
    baseConfig.BASE_URL = '"http://test.admin2.intramirror.com/"';
    baseConfig.ERROR_URL = '"http://test.vendor.intramirror.com:8082/"';
    baseConfig.ERROR_URL2 = '"http://test.api.intramirror.com:8088/"';
    break;
  case "stag": 
    baseConfig.BASE_URL = '"http://sha.staging.admin2.intramirror.com:8096/"';
    baseConfig.API_URL = '"http://sha.staging.api.intramirror.com/"';
    baseConfig.ERROR_URL = '"http://sha.staging.vendor.intramirror.com:8092"';
    baseConfig.ERROR_URL2 = '"http://101.132.132.34:8095"';
    break;
}
module.exports = baseConfig;
