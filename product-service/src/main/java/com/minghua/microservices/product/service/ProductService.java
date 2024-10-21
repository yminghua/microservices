package com.minghua.microservices.product.service;

import com.minghua.microservices.product.dto.ProductRequest;
import com.minghua.microservices.product.dto.ProductResponse;
import com.minghua.microservices.product.model.Product;
import com.minghua.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product created successfully");
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }

    public List<ProductResponse> getAllProducts() {
         return productRepository.findAll()
                 .stream()
                 .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice()))
                 .toList();
    }
}
