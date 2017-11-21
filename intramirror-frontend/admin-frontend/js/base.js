var baseUrl = "http://test.admin.intramirror.com:8096/";


let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}

token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxOTQiLCJpYXQiOjE1MTEyNTQxODJ9.ZQCbot3d8UlyNAG6sUcwlngE9VY1FnMOKiLosE91xriLBtd96VFChvd-4D6p-ALnS8o6L1nHzOyxfNPLT1QSmg";