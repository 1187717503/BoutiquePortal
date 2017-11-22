var baseUrl = "http://test.admin.intramirror.com:8096/";


let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}

token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyNjEiLCJpYXQiOjE1MTE0MjQ4MzB9.-xQ8H_03BWP9pNmbV5wWNNrDd2dYb-noN4vEj9xf5fdAFa1uO4PZRiQflFP8bt7z81m4eG-6EF2JKk3LCIZcBw";