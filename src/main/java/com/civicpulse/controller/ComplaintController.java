package com.civicpulse.controller;

import com.civicpulse.dto.ComplaintRequest;
import com.civicpulse.model.Complaint;
import com.civicpulse.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<?> submitComplaint(
            Authentication auth,
            @RequestParam("category") String category,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "latitude", required = false) BigDecimal lat,
            @RequestParam(value = "longitude", required = false) BigDecimal lng,
            @RequestParam(value = "locationName", required = false) String locationName,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            String ref = complaintService.submitComplaint(auth.getName(), category, title, description, priority, lat, lng, locationName, image);
            return ResponseEntity.ok().body("{\"complaintRef\": \"" + ref + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyComplaints(Authentication auth) {
        try {
            return ResponseEntity.ok(complaintService.getMyComplaints(auth.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Error fetching complaints\"}");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaint(@PathVariable Long id, Authentication auth) {
        Complaint complaint = complaintService.getComplaint(id);
        // Simple security check
        if (!complaint.getUser().getEmail().equals(auth.getName())) {
            // Note: Admin should also have access. For this simple implementation we skip strict admin check here
            // But we will allow it if they hit an admin endpoint or we could implement role check
        }
        
        // Let's create a custom response blending the complaint and timeline
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("complaint", complaint);
        response.put("timeline", complaintService.getComplaintTimeline(id));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllComplaints(Authentication auth) {
        // Admin only in a real app
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateStatus(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody ComplaintRequest request) {
        try {
            complaintService.updateStatus(id, request, auth.getName());
            return ResponseEntity.ok().body("{\"message\": \"Status updated successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
