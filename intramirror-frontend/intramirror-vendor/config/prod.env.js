const baseConfig = {
  NODE_ENV: '"production"',
  BASE_URL:''
};
switch (process.env.WEB_ENV){
  case 'loca':
    baseConfig.BASE_URL = '"http://localhost:8080/"';
    break;
  case 'test':
    baseConfig.BASE_URL = '"http://test.vendor.intramirror.com:8097/"';
    break;
  case 'stag':
    baseConfig.BASE_URL = '"http://sha.staging.boutique.intramirror.com:8090/"';
    break;
  case 'prd':
    baseConfig.BASE_URL = '"http://boutique.intramirror.com:8087/"';
    break;
}
module.exports = baseConfig;
