package com.templates.security.controller;

import com.templates.security.dto.ApiResponse;
import com.templates.security.dto.ProductDto;
import com.templates.security.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua_parser.Parser;
import ua_parser.Client;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final Parser uaParser = new Parser();
    @PostMapping
    public ApiResponse<ProductDto> createProduct(@RequestBody ProductDto dto, HttpServletRequest request){
        String remoteUser = request.getRemoteUser();
        System.out.println(request.getRemoteAddr());
        System.out.println(remoteUser);
        String userAgent = request.getHeader("User-Agent");
        System.out.println("User-Agent: " + userAgent);

        Client client = uaParser.parse(request.getHeader("User-Agent"));
        String device = client.device.family; // e.g., iPhone, Desktop, etc.
        System.out.println(device);

        String os = client.os.family; // e.g., Windows, iOS, etc.
        System.out.println(os);

        String browser = client.userAgent.family;
        System.out.println(browser);

        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        System.out.println(principal.toString());
        return productService.createProduct(dto);
    }
}
