package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.config.AppConstant;
import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.dto.WarehouseAnalyticsDTO;
import com.ecommerce.hyperlocaldelivery.dto.WarehouseDTO;
import com.ecommerce.hyperlocaldelivery.entity.Order;
import com.ecommerce.hyperlocaldelivery.entity.OrderStatus;
import com.ecommerce.hyperlocaldelivery.entity.Product;
import com.ecommerce.hyperlocaldelivery.entity.Warehouse;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.repository.OrderRepository;
import com.ecommerce.hyperlocaldelivery.repository.UserRepository;
import com.ecommerce.hyperlocaldelivery.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements IWarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Warehouse getWarehouseByCity(String city) {
        if (!AppConstant.SUPPORTED_CITIES.contains(city)) {
            throw new IllegalArgumentException("Service not available in this city: " + city);
        }
        return warehouseRepository.findByCity(city)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found for city: " + city));
    }

    @Override
    public Warehouse getWarehouseById(Integer warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));
    }

    @Override
    public List<ProductDTO> getLowStockProducts(Integer warehouseId) {
        Warehouse warehouse = getWarehouseById(warehouseId);
        return productService.getProductsByWarehouse(warehouse).stream()
                .filter(product -> product.getQuantity() < 10) // Assuming low stock is < 10
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseAnalyticsDTO getWarehouseAnalytics(Integer warehouseId, Integer days) {
        Warehouse warehouse = getWarehouseById(warehouseId);
        LocalDateTime startDate = LocalDateTime.now().minus(days - 1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endDate = LocalDateTime.now();
        List<Order> orders = orderRepository.findByWarehouseAndCreatedAtAfter(warehouse, startDate);

        double totalSales = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
        long ordersCount = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.DELIVERED)
                .count();
        double averageSalesPerDay = days > 0 ? totalSales / days : 0.0;

        return WarehouseAnalyticsDTO.builder()
                .warehouseId(warehouse.getWarehouseId())
                .warehouseName(warehouse.getName())
                .city(warehouse.getCity())
                .totalSales(totalSales)
                .orderCount(ordersCount)
                .averageSalesPerDay(averageSalesPerDay)
                .days(days)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Override
    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(warehouse -> WarehouseDTO.builder()
                        .warehouseId(warehouse.getWarehouseId())
                        .name(warehouse.getName())
                        .city(warehouse.getCity())
                        .managerId(warehouse.getManager() != null ? warehouse.getManager().getUserId() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        if (warehouseRepository.existsByCity(warehouseDTO.getCity())) {
            throw new InvalidOperationException("Warehouse already exists for city: " + warehouseDTO.getCity());
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseDTO.getName());
        warehouse.setCity(warehouseDTO.getCity());
        if (warehouseDTO.getManagerId() != null) {
            com.ecommerce.hyperlocaldelivery.entity.User manager = userRepository.findById(warehouseDTO.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + warehouseDTO.getManagerId()));
            if (manager.getRole() != com.ecommerce.hyperlocaldelivery.entity.Role.WAREHOUSE) {
                throw new InvalidOperationException("Manager must have role WAREHOUSE");
            }
            warehouse.setManager(manager);
        }

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return WarehouseDTO.builder()
                .warehouseId(savedWarehouse.getWarehouseId())
                .name(savedWarehouse.getName())
                .city(savedWarehouse.getCity())
                .managerId(savedWarehouse.getManager() != null ? savedWarehouse.getManager().getUserId() : null)
                .build();
    }
}