var baseUrl = "http://admin2.intramirror.com:8085/";
var refreshImage = "http://106.15.201.83:8204/";
let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}
