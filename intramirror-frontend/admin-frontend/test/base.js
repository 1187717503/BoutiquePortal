var baseUrl = "http://test.admin.intramirror.com:8096/";
var refreshImage = "http://106.15.229.248:8204/";
let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}
