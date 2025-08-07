package com.templates.security.service;

import com.templates.security.domain.Product;
import com.templates.security.domain.Users;
import com.templates.security.dto.ApiResponse;
import com.templates.security.dto.ProductDto;
import com.templates.security.repository.ProductRepository;
import com.templates.security.repository.UserRepository;
import com.templates.security.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ApiResponse<ProductDto> createProduct(ProductDto dto){
        Optional<Users> optional = userRepository.findById(dto.getUserId());
        if (optional.isEmpty())
            return ApiResponse.<ProductDto>builder()
                    .success(false)
                    .message(String.format("Users with %d id is not found", dto.getUserId()))
                    .build();

        Product entity = productMapper.toEntity(dto, optional.get());

        Product product = productRepository.save(entity);

        return ApiResponse.<ProductDto>builder()
                .success(true)
                .content(productMapper.toDto(product))
                .message("Successfully saved product")
                .build();
    }
}
