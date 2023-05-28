package com.ms.inventoryservice;

import jakarta.persistence.*;
import lombok.*;
import org.apache.coyote.Response;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static jakarta.persistence.GenerationType.IDENTITY;
import static org.springframework.http.HttpStatus.OK;

@SpringBootApplication
public class InventoryServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			inventoryRepository.deleteAll();

			Inventory inventory0 = new Inventory();
			inventory0.setSkuCode("Galaxy_watch_4");
			inventory0.setQuantity(1000);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("Galaxy_watch_5");
			inventory1.setQuantity(0);

			inventoryRepository.save(inventory0);
			inventoryRepository.save(inventory1);
		};
	}
}

@Controller
@RequestMapping("inventory")
@RequiredArgsConstructor
class InventoryController {

	private final InventoryService inventoryService;

	@GetMapping
	public ResponseEntity<List<InventoryResponse>> isInStock(@RequestParam("sku-code") List<String> skuCodes) {
		return ResponseEntity.ok(inventoryService.isInStock(skuCodes));
	}
}

@Service
@RequiredArgsConstructor
class InventoryService {

	private final InventoryRepository inventoryRepository;

	@Transactional(readOnly = true)
	public List<InventoryResponse> isInStock(List<String> skuCodes) {
		return inventoryRepository.findBySkuCodeIn(skuCodes)
				.stream()
				.map(this::toInventoryResponse)
				.toList();
	}

	private InventoryResponse toInventoryResponse(Inventory inventory) {
		return InventoryResponse.builder()
				.skuCode(inventory.getSkuCode())
				.isInStock(inventory.getQuantity() > 0)
				.build();
	}
}

@Repository
interface InventoryRepository extends JpaRepository<Inventory, Long> {
	List<Inventory> findBySkuCodeIn(List<String> skuCodes);
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "INVENTORY")
class Inventory {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	private String skuCode;
	private Integer quantity;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class InventoryResponse {
	private String skuCode;
	private Boolean isInStock;
}
