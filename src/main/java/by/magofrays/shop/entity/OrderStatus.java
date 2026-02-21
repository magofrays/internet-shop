package by.magofrays.shop.entity;

public enum OrderStatus {
    NEW,                    // только создан
    PENDING_PAYMENT,        // ожидает оплаты
    PAID,                   // оплачен
    CONFIRMED,              // подтвержден магазином
    PROCESSING,             // собирается
    SHIPPED,                // передан в доставку
    DELIVERED,              // доставлен
    COMPLETED,              // завершен
    CANCELLED,              // отменен
    REFUNDED                // возврат
}
