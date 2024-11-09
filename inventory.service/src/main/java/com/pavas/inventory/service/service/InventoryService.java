package com.pavas.inventory.service.service;

import com.pavas.inventory.service.dto.InventoryResponse;
import com.pavas.inventory.service.model.Inventory;
import com.pavas.inventory.service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> codes) {
        return inventoryRepository.findByCodeIn(codes)
                .stream()
                .map(this::mapToInventoryResponse)
                .toList();
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .code(inventory.getCode())
                .isInStock(inventory.getQuantity() > 0)
                .build();
    }
}
