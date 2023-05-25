package com.ms.productsevice;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
class ProductController {

	private final ProductService productService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createProduct(@RequestBody ProductRequest productRequest) {
		productService.createProduct(productRequest);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ProductResponse> getAllProducts() {
		return productService.getAllProducts();
	}
}

@Slf4j
@Service
@RequiredArgsConstructor
class ProductService {

	private final ProductRepository productRepository;
	public void createProduct(ProductRequest productRequest) {

		Product product = Product.builder()
				.name(productRequest.getName())
				.description(productRequest.getDescription())
				.price(productRequest.getPrice())
				.build();

		productRepository.save(product);

		log.info("Product {} saved", product.getId());
	}

	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll()
				.stream()
				.map(this::mapToProductResponse)
				.toList();
	}

	private ProductResponse mapToProductResponse(Product product) {
		return ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.build();
	}
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ProductRequest {
	private String name;
	private String description;
	private BigDecimal price;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ProductResponse {
	private String id;
	private String name;
	private String description;
	private BigDecimal price;
}

interface ProductRepository extends MongoRepository<Product, String> {}

@Document(value = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
class Product {
	@Id
	private String id;
	private String name;
	private String description;
	private BigDecimal price;
}
