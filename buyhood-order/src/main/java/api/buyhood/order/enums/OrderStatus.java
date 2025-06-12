package api.buyhood.order.enums;

public enum OrderStatus {
	//공용
	PENDING,
	CANCELED,
	ACCEPTED,
	REJECTED,
	COMPLETED,

	//배달가능
	DELIVERING,
	DELIVERED,
	//ONLY 픽업
	READY
}
