package com.pavas.inventory.service.controller;

import com.pavas.inventory.service.dto.InventoryResponse;
import com.pavas.inventory.service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> codes) {
        return inventoryService.isInStock(codes);
    }
}
