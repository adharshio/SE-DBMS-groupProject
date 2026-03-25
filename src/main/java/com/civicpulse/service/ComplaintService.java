package com.civicpulse.service;

import com.civicpulse.dto.ComplaintRequest;
import com.civicpulse.model.Complaint;
import com.civicpulse.model.ComplaintTimeline;
import com.civicpulse.model.Notification;
import com.civicpulse.model.User;
import com.civicpulse.repository.ComplaintRepository;
import com.civicpulse.repository.ComplaintTimelineRepository;
import com.civicpulse.repository.NotificationRepository;
import com.civicpulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ComplaintTimelineRepository timelineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String submitComplaint(String email, String category, String title, String description,
                                  String priority, BigDecimal lat, BigDecimal lng, String locationName, MultipartFile image) throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setCategory(category);
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setPriority(priority != null ? priority : "MEDIUM");
        complaint.setLatitude(lat);
        complaint.setLongitude(lng);
        complaint.setLocationName(locationName);
        
        String ref = "CP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        complaint.setComplaintRef(ref);

        if (image != null && !image.isEmpty()) {
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, image.getBytes());
            complaint.setImageUrl("/uploads/" + fileName);
        }

        complaint = complaintRepository.save(complaint);

        // Add to timeline
        ComplaintTimeline timeline = new ComplaintTimeline();
        timeline.setComplaint(complaint);
        timeline.setStatus("SUBMITTED");
        timeline.setNote("Complaint submitted successfully.");
        timeline.setUpdatedBy(user.getName());
        timelineRepository.save(timeline);

        return ref;
    }

    public List<Complaint> getMyComplaints(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return complaintRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public Complaint getComplaint(Long id) {
        return complaintRepository.findById(id).orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    public List<ComplaintTimeline> getComplaintTimeline(Long complaintId) {
        return timelineRepository.findByComplaintIdOrderByCreatedAtDesc(complaintId);
    }

    public void updateStatus(Long id, ComplaintRequest request, String adminEmail) {
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new RuntimeException("Complaint not found"));
        User admin = userRepository.findByEmail(adminEmail).orElseThrow(() -> new RuntimeException("Admin user not found"));

        complaint.setStatus(request.getStatus());
        if (request.getAssignedDept() != null) {
            complaint.setAssignedDept(request.getAssignedDept());
        }
        complaintRepository.save(complaint);

        // Add timeline entry
        ComplaintTimeline timeline = new ComplaintTimeline();
        timeline.setComplaint(complaint);
        timeline.setStatus(request.getStatus());
        timeline.setNote(request.getNote());
        timeline.setUpdatedBy(admin.getName());
        timelineRepository.save(timeline);

        // Add notification for the user
        Notification notification = new Notification();
        notification.setUser(complaint.getUser());
        notification.setTitle("Update on your complaint: " + complaint.getComplaintRef());
        notification.setBody("Status changed to " + request.getStatus() + ". Note: " + request.getNote());
        notificationRepository.save(notification);
    }
}
