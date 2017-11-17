var baseUrl = "http://sha.staging.admin2.intramirror.com:8096/";

let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}
