package com.example.toyprojectback.controller;


import com.example.toyprojectback.entity.BoardCategory;
import com.example.toyprojectback.entity.User;
import com.example.toyprojectback.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final BoardService boardService;
    .findAllByNickName;

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("userJoinRequest", new UserJoinRequest());
        return "users/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute UserJoinRequest req, BindingResult bindingResult, Model model) {
        if (userService.joinValid(req, bindingResult).hasErrors()) { // 에러가 있으면 다시 회원가입 페이지로 이동 시킨다.
            return "users/join";
        }

        userService.join(req);

        model.addAttribute("message", "회원 가입에 성공 했습니다.! \n로그인 후 사용 가능 합니다!");
        model.addAttribute("nextUrl", "/login");

        return "printMessage";
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) {

        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/login") && !uri.contains("/join")) {
            request.getSession().setAttribute("prevPage", uri);
        }
        model.addAttribute("userLoginRequest", new UserLoginRequest());
        return "users/login";
    }

    @GetMapping("/mypage/{category}")
    public String myPage(@PathVariable String category, Authentication auth, Model model) {
        model.addAttribute("boards", boardService.findMyBoard(category, auth.getName()));
        model.addAttribute("category", category);
        model.addAttribute("user", userService.myInfo(auth.getName()));
        return "users/mypage";
    }

    @GetMapping("/edit")
    public String userEditPage(Authentication auth, Model model) {
        User user = userService.myInfo(auth.getName());
        model.addAttribute("userDto", UserDto.of(user));
        return "users/edit";
    }

    @PostMapping("/edit")
    public String userEdit(@Valid @ModelAttribute UserDto userDto, BindingResult bindingResult, Authentication auth, Model model) {
        if (userService.editValid(dto, bindingResult, auth.getName()).hasErrors()) {
            return "users/edit";
        }
        userService.edit(dto, auth.getName());

        model.addAttribute("message", "정보가 수정 되었습니다");
        model.addAttribute("nextUrl", "/users/myPage/board");
        return "printMessage";
    }

    @GetMapping("/delete")
    public String userDeeletePage(Authentication authentication, Model model) {
        User user = userService.myInfo(authentication.getName());
        model.addAttribute("userDto", UserDto.of(user));
        return "users/delete";
    }

    @PostMapping("/delete")
    public String userDelete(@ModelAttribute UserDto dto, Authentication auth, Model model) {
        Boolean deleteSucess = userService.delete(auth.getName(), auth.getNowPassword());
        if (deleteSucess) {
            model.addAttribute("message", "탈퇴 되었습니다");
            model.addAttribute("nextUrl", "/users/logout");
            return "printMessage";
        } else {
            model.addAttribute("message", "비밀번호가 일치하지 않습니다");
            model.addAttribute("nextUrl", "/users/delete");
            return "printMessage";
        }
    }

        @GetMapping("/admin")
        public String adminPage(@RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "") String keyword,
        Model model) {

            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
            Page<User> users = userService.findAllByNickname(keyword, pageRequest);

            model.addAttribute("users", users);
            model.addAttribute("keyword", keyword);
            return "users/admin";
        }

        @GetMapping("/admin/{userId}")
        public String adminChangRole(@PathVariable Long userId,@RequestParam(required= false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "") String keyword) throws UnsupportedEncodingException {
            userService.changeRole(userId);
            return "redirect:/users/admin?page=" + page + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
        }


