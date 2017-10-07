const baseConfig = {
  NODE_ENV: '"development"',
  BASE_URL:''
};
switch (process.env.WEB_ENV){
  case 'loca':
    baseConfig.BASE_URL = '"http://localhost:8080/"';
    break;
  case 'test':
    baseConfig.BASE_URL = '"http://test.vendor.intramirror.com:8087/"';
    break;
}
module.exports = baseConfig;
