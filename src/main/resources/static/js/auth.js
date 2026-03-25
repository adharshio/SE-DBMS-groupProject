async function login(email, password) {
  try {
    const data = await apiPost("/api/auth/login", { email, password });
    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify({
      id: data.id, name: data.name,
      email: data.email, citizenId: data.citizenId
    }));
    window.location.href = "/home.html";
  } catch (err) {
    throw err;
  }
}

function logout() {
  localStorage.clear();
  window.location.href = "/index.html";
}

function getCurrentUser() {
  const u = localStorage.getItem("user");
  return u ? JSON.parse(u) : null;
}

async function register(name, email, password, phone) {
  try {
    await apiPost("/api/auth/register", { name, email, password, phone });
    await login(email, password);
  } catch (err) {
    throw err;
  }
}
