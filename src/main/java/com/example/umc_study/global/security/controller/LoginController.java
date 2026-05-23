package com.example.umc_study.global.security.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @ResponseBody
    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout) {
        String notice = "";

        if (error != null) {
            notice = "<p style=\"color:#b42318;\">email or password is invalid.</p>";
        } else if (logout != null) {
            notice = "<p style=\"color:#067647;\">you have been logged out.</p>";
        }

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>UMC Login</title>
                </head>
                <body style="font-family: Arial, sans-serif; margin: 40px; background: #f7f8fa;">
                    <div style="max-width: 420px; margin: 0 auto; background: #ffffff; padding: 32px; border-radius: 16px; box-shadow: 0 12px 30px rgba(16, 24, 40, 0.08);">
                        <h1 style="margin-top: 0;">Login</h1>
                        <p style="color: #475467;">Use the email and password you created at signup.</p>
                        %s
                        <form method="post" action="/login" style="display: grid; gap: 12px;">
                            <label for="email">Email</label>
                            <input id="email" name="email" type="email" placeholder="tester@example.com" required
                                   style="padding: 12px; border: 1px solid #d0d5dd; border-radius: 8px;">
                            <label for="password">Password</label>
                            <input id="password" name="password" type="password" placeholder="password" required
                                   style="padding: 12px; border: 1px solid #d0d5dd; border-radius: 8px;">
                            <button type="submit"
                                    style="margin-top: 8px; padding: 12px; border: 0; border-radius: 8px; background: #111827; color: #ffffff; font-weight: 600; cursor: pointer;">
                                Sign in
                            </button>
                        </form>
                        <div style="margin: 20px 0; display: flex; align-items: center; gap: 12px;">
                            <div style="height: 1px; flex: 1; background: #eaecf0;"></div>
                            <span style="color: #98a2b3; font-size: 14px;">or</span>
                            <div style="height: 1px; flex: 1; background: #eaecf0;"></div>
                        </div>
                        <a href="/oauth/authorize/kakao"
                           style="display: block; text-align: center; text-decoration: none; padding: 12px; border-radius: 8px; background: #fee500; color: #191919; font-weight: 700;">
                            Continue with Kakao
                        </a>
                    </div>
                </body>
                </html>
                """.formatted(notice);
    }
}
