var baseUrl = "http://localhost:8080/";


let token = sessionStorage.getItem('token');
if (!token) {
    token = localStorage.getItem('token');
}

token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyNjEiLCJpYXQiOjE1MTE2NjY5MzB9.RKA5HdiFcrTHUzzJB2zkZlgKZq5eedQSlgqUY9uq-3QVJeb9oQvccOfhlmIsx9Bgye3Lgyy2YIdKu-UBkINDrQ";