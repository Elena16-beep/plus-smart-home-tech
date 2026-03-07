package ru.yandex.practicum.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<OrderBooking, UUID> {
    Optional<OrderBooking> findBookingByOrderId(UUID orderId);
}