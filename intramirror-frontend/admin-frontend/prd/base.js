var baseUrl = "http://admin2.intramirror.com:8085/";

let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}
