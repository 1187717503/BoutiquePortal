const baseConfig = {
  NODE_ENV: '"development"',
  BASE_URL:''
};
switch (process.env.WEB_ENV){
  case 'loca':
    baseConfig.BASE_URL = '"http://localhost:8080/intramirror_vendor_web/"';
    break;
  case 'test':
    baseConfig.BASE_URL = '"http://test.vendor.intramirror.com:8087/intramirror_vendor_web/"';
    break;
}
module.exports = baseConfig;
