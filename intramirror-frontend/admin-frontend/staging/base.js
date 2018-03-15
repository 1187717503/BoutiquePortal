var baseUrl = "http://sha.staging.admin2.intramirror.com:8096/";
var refreshImage = "http://106.15.229.248:8204/";
let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}
