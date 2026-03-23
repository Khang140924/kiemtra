package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enroll")
public class EnrollController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/{id}")
    public String enrollCourse(@PathVariable("id") Long courseId, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        
        // KIỂM TRA QUYỀN TRỰC TIẾP: Chặn tài khoản mang quyền ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
             redirectAttributes.addFlashAttribute("errorMsg", "Tài khoản ADMIN không được phép đăng ký học phần!");
             return "redirect:/home";
        }

        Student student = studentRepository.findByUsername(username);
        
        Course course = courseRepository.findById(courseId).orElse(null);
        
        if (student != null && course != null) {
            // Kiểm tra xem đã đăng ký chưa
            if (student.getCourses().contains(course)) {
                redirectAttributes.addFlashAttribute("errorMsg", "Bạn đã đăng ký học phần này rồi!");
            } else {
                student.getCourses().add(course);
                studentRepository.save(student);
                redirectAttributes.addFlashAttribute("successMsg", "Đăng ký thành công học phần: " + course.getName());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy học phần hoặc tài khoản không hợp lệ!");
        }
        
        return "redirect:/home";
    }
}
