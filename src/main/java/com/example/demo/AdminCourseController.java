package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    // 1. Hiển thị danh sách môn học (Dành cho Admin quản lý)
    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "admin/course-list";
    }

    // 2. Hiển thị Form Thêm mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new Course());
        return "admin/course-form";
    }

    // 3. Xử lý lưu dữ liệu (Dùng chung cho cả Thêm mới và Sửa) có xử lý File Upload
    @PostMapping("/save")
    public String saveCourse(@ModelAttribute("course") Course course, 
                             @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile file) {
        
        // Nếu người dùng có đính kèm file ảnh
        if (file != null && !file.isEmpty()) {
            try {
                // Tạo thư mục uploads/ nếu chưa có
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/");
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                
                // Tránh trùng tên ảnh bằng cách nối thêm thời gian hiện tại
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                
                // Copy file từ request vào thư mục uploads/
                java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                // Cập nhật tên ảnh vào Entity Course
                course.setImage(fileName);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        
        courseRepository.save(course);
        return "redirect:/admin/courses"; // Lưu xong quay về trang danh sách
    }

    // 4. Hiển thị Form Sửa (Lấy dữ liệu cũ đắp lên form)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy môn học có ID: " + id));
        model.addAttribute("course", course);
        return "admin/course-form";
    }

    // 5. Xử lý Xóa triệt để (Fix lỗi 500 Constraint Violation)
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null) {
            // Bước quan trọng: Gỡ bỏ môn học này khỏi tất cả các sinh viên đã lỡ enroll
            // để không bị lỗi rành buộc khoá ngoại (Foreign Key Constraint) bảng student_course
            List<Student> students = studentRepository.findAll();
            for (Student s : students) {
                if (s.getCourses().contains(course)) {
                    s.getCourses().remove(course);
                    studentRepository.save(s);
                }
            }
            
            // Sau khi gỡ khoá ngoại, mới tiến hành xoá Course
            courseRepository.deleteById(id);
        }
        return "redirect:/admin/courses";
    }

    // 6. Fix lỗi User Admin lỡ Enroll từ đầu
    @GetMapping("/clear-admin-enroll")
    public String clearAdminEnroll() {
        List<Student> students = studentRepository.findAll();
        for (Student s : students) {
            boolean isAdmin = s.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                // Xoá sạch danh sách các môn đã lỡ đăng ký của Admin
                s.getCourses().clear();
                studentRepository.save(s);
            }
        }
        return "redirect:/admin/courses";
    }
}