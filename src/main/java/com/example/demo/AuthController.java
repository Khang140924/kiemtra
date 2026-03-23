package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired private StudentRepository studentRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Hiển thị trang đăng nhập
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Hiển thị trang đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    // Xử lý lưu đăng ký
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("student") Student student) {
        // 1. Mã hóa mật khẩu
        student.setPassword(passwordEncoder.encode(student.getPassword()));

        // 2. Tìm quyền STUDENT trong DB, nếu chưa có thì tự tạo
        Role role = roleRepository.findByName("STUDENT");
        if (role == null) {
            role = new Role();
            role.setName("STUDENT");
            roleRepository.save(role);
        }
        
        // 3. Gán quyền và lưu sinh viên
        student.getRoles().add(role);
        studentRepository.save(student);

        return "redirect:/login?registered"; // Đăng ký xong đẩy về trang login
    }
}