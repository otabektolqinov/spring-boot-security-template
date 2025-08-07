package com.templates.security.service.mapper;

import com.templates.security.domain.Product;
import com.templates.security.domain.Users;
import com.templates.security.dto.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductDto dto, Users users){
        return Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .users(users)
                .build();
    }

    public ProductDto toDto(Product product){
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .userId(product.getUsers().getId())
                .build();
    }
}
