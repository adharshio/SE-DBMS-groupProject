async function loadStats() {
  try {
    const complaints = await apiGet("/api/complaints/my");
    document.getElementById("total-submitted").innerText = complaints.length;
    document.getElementById("in-progress").innerText = complaints.filter(c => c.status === 'IN_PROGRESS').length;
    document.getElementById("resolved").innerText = complaints.filter(c => c.status === 'RESOLVED').length;
  } catch(e) { console.error(e); }
}

async function loadRecentComplaints() {
  try {
    const complaints = await apiGet("/api/complaints/my");
    const container = document.getElementById("recent-list");
    if(!container) return;
    container.innerHTML = "";
    complaints.slice(0, 3).forEach(c => {
      container.innerHTML += `
        <div class="card">
          <h4>${c.title}</h4>
          <span class="badge badge-${c.status.toLowerCase().replace('_', '')}">${c.status}</span>
          <p>${new Date(c.createdAt).toLocaleDateString()}</p>
        </div>
      `;
    });
  } catch(e) { console.error(e); }
}

async function submitComplaint(e) {
  e.preventDefault();
  const form = document.getElementById("submit-form");
  const formData = new FormData(form);
  try {
    document.getElementById("submit-btn").disabled = true;
    const res = await apiPostForm("/api/complaints", formData);
    document.getElementById("success-screen").style.display = "block";
    document.getElementById("ref-number").innerText = res.complaintRef;
    form.style.display = "none";
  } catch(err) {
    alert("Error submitting complaint");
    document.getElementById("submit-btn").disabled = false;
  }
}

async function loadAllComplaints() {
  try {
    const complaints = await apiGet("/api/complaints/my");
    const container = document.getElementById("complaints-list");
    if(!container) return;
    container.innerHTML = "";
    complaints.forEach(c => {
      container.innerHTML += `
        <div class="card" onclick="loadComplaintDetail(${c.id})">
          <h4>${c.title}</h4>
          <span class="badge badge-${c.status.toLowerCase().replace('_', '')}">${c.status}</span>
          <p>${c.category} - ${c.locationName}</p>
        </div>
      `;
    });
  } catch(e) {}
}

async function loadComplaintDetail(id) {
  window.location.href = `/track.html?id=${id}`;
}

async function detectLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(position => {
      document.getElementById("latitude").value = position.coords.latitude;
      document.getElementById("longitude").value = position.coords.longitude;
      document.getElementById("locationName").value = "Detected Location";
      alert("Location detected!");
    });
  } else {
    alert("Geolocation is not supported by this browser.");
  }
}

async function loadNavBadge() {
  try {
    const res = await apiGet("/api/notifications/unread-count");
    const badge = document.getElementById("nav-notif-badge");
    if(badge && res.count > 0) {
      badge.innerText = res.count;
      badge.style.display = "inline-block";
    }
  } catch(e){}
}

document.addEventListener("DOMContentLoaded", () => {
  const user = getCurrentUser();
  if (user && document.getElementById("user-name")) {
    document.getElementById("user-name").innerText = user.name;
    loadNavBadge();
  }
});
