var baseUrl = "http://localhost:8080/";


let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}

token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyNjEiLCJpYXQiOjE1MTE5MjU4Mjh9.PXBYGkx97C51NGAH9-TlI7vZfEssh3jaRdMrw8EPhZCfXw_GPqvbb0S7z5uf8zOq7qZwHZ2cT2PxtH8TWvv9fw";
