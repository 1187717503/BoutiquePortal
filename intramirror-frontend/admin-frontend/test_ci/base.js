var baseUrl = "http://test.admin.intramirror.com:8099/";

let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}
