package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Câu 8: Xử lý tìm kiếm học phần theo tên và phân trang
    @GetMapping({"/", "/home", "/courses"})
    public String home(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {
        
        int pageSize = 5; 
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize);
        
        Page<Course> coursePage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Có từ khoá tìm kiếm
            coursePage = courseRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
            model.addAttribute("keyword", keyword.trim());
        } else {
            // Không có từ khoá tìm kiếm
            coursePage = courseRepository.findAll(pageable);
        }

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", coursePage.getTotalPages());

        return "home"; 
    }

    // Câu 7: Hiển thị danh sách học phần sinh viên đã đăng ký
    @GetMapping("/my-courses")
    public String myCourses(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        Student student = studentRepository.findByUsername(username);
        
        if (student != null) {
            model.addAttribute("courses", student.getCourses());
        }
        
        return "my-courses";
    }
}