const BASE_URL = "";

function getToken() {
  return localStorage.getItem("token");
}

function authHeaders() {
  return {
    "Authorization": "Bearer " + getToken(),
    "Content-Type": "application/json"
  };
}

function requireAuth() {
  if (!getToken()) window.location.href = "/index.html";
}

async function apiPost(url, body) {
  const res = await fetch(BASE_URL + url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  if (!res.ok) throw await res.json();
  return res.json();
}

async function apiGet(url) {
  const res = await fetch(BASE_URL + url, { headers: authHeaders() });
  if (res.status === 401) { localStorage.clear(); window.location.href = "/index.html"; }
  if (!res.ok) throw await res.json();
  return res.json();
}

async function apiPut(url, body) {
  const res = await fetch(BASE_URL + url, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(body)
  });
  if (!res.ok) throw await res.json();
  if (res.status === 204) return {}; // Handle No Content
  const text = await res.text();
  return text ? JSON.parse(text) : {};
}

async function apiPostForm(url, formData) {
  const res = await fetch(BASE_URL + url, {
    method: "POST",
    headers: { "Authorization": "Bearer " + getToken() },
    body: formData
  });
  if (!res.ok) throw await res.json();
  return res.json();
}
