package com.pavas.inventory.service.repository;

import com.pavas.inventory.service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByCodeIn(List<String> skuCode);
}
