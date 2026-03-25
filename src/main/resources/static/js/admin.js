async function loadAllAdminComplaints() {
  try {
    const complaints = await apiGet("/api/complaints/admin/all");
    const tbody = document.getElementById("admin-complaints-body");
    if(!tbody) return;
    tbody.innerHTML = "";
    
    document.getElementById("stat-total").innerText = complaints.length;
    document.getElementById("stat-progress").innerText = complaints.filter(c => c.status === 'IN_PROGRESS').length;
    
    complaints.forEach(c => {
      const isOverdue = new Date(c.createdAt) < new Date(Date.now() - 7 * 24 * 60 * 60 * 1000) && c.status !== 'RESOLVED';
      const rowClass = isOverdue ? 'style="background-color: #ffebe9;"' : '';
      tbody.innerHTML += `
        <tr ${rowClass}>
          <td>${c.complaintRef}</td>
          <td>${c.title}<br/><small>${c.locationName}</small></td>
          <td>${c.category}</td>
          <td><span class="badge badge-${c.status.toLowerCase().replace('_', '')}">${c.status}</span></td>
          <td>${c.priority}</td>
          <td>${c.assignedDept || 'Unassigned'}</td>
          <td><button class="btn-secondary" onclick="openModal(${c.id}, '${c.status}')">Update</button></td>
        </tr>
      `;
    });
  } catch(e) { console.error(e); }
}

let activeComplaintId = null;

function openModal(id, currentStatus) {
  activeComplaintId = id;
  document.getElementById("update-modal").style.display = "block";
  document.getElementById("status-select").value = currentStatus;
  document.getElementById("note-input").value = "";
}

function closeModal() {
  document.getElementById("update-modal").style.display = "none";
}

async function updateStatus() {
  const status = document.getElementById("status-select").value;
  const note = document.getElementById("note-input").value;
  if(!note) return alert("Note is required");
  try {
    await apiPut(`/api/complaints/admin/${activeComplaintId}/status`, { status, note });
    closeModal();
    loadAllAdminComplaints();
  } catch(e) {
    alert("Error updating status");
  }
}
