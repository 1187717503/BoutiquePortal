var baseUrl = "http://test.admin.intramirror.com:8096/";

let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}
