package com.ms.inventoryservice;

import com.ms.inventoryservice.model.Inventory;
import com.ms.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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



