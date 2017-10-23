// module.exports = {
//   NODE_ENV: '"production"'
// }
const baseConfig = {
  NODE_ENV: '"production"',
  BASE_URL: '',
  ERROR_URL:'',
  ERROR_URL2:''
};
switch (process.env.WEB_ENV){
  case "test":
    baseConfig.BASE_URL = '"http://test.admin.intramirror.com:8085/"';
    baseConfig.ERROR_URL = '"http://test.vendor.intramirror.com/"';
    baseConfig.ERROR_URL2 = '"http://test.api.intramirror.com/"';
    break;
  case "stag":
  baseConfig.BASE_URL = '"http://sha.staging.admin2.intramirror.com:8096/"';
  baseConfig.ERROR_URL = '"http://sha.staging.vendor.intramirror.com/"';
  baseConfig.ERROR_URL2 = '"http://101.132.132.34:8095"';
    break;
  case "prd":
    baseConfig.BASE_URL = '"http://admin2.intramirror.com:8085/"';
    baseConfig.ERROR_URL = '"http://vendor.intramirror.com/"';
    baseConfig.ERROR_URL2 = '"http://api.intramirror.com/"';
    break;
}
module.exports = baseConfig;