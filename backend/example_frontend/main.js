document.addEventListener("DOMContentLoaded", () => {
  const loginBtn = document.getElementById("loginBtn");

  loginBtn.onclick = () => {
    window.location.href = "http://localhost:3000/auth/login";  // This will handle the redirect
  };
});
