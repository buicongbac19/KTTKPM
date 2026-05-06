import { apiRequest } from "@/services/http";
import type { Ban, BanDat, MonAn } from "@/types/entities";

export function getListTable(): Promise<Ban[]> {
  return apiRequest<Ban[]>("/api/orders/tables");
}

export function getOrCreateReservationTable(tableNumber: number): Promise<BanDat> {
  return apiRequest<BanDat>(`/api/orders/reservation-tables/${tableNumber}`, {
    method: "POST",
  });
}

export function getReservationTable(reservationTableId: number): Promise<BanDat> {
  return apiRequest<BanDat>(`/api/orders/reservation-tables/${reservationTableId}`);
}

export function getDishesByName(keyword: string): Promise<MonAn[]> {
  return apiRequest<MonAn[]>("/api/orders/dishes", {
    query: {
      keyword,
    },
  });
}

export function addItem(
  reservationTableId: number,
  monAnId: number,
  soLuong: number,
): Promise<BanDat> {
  return apiRequest<BanDat>(`/api/orders/reservation-tables/${reservationTableId}/items`, {
    method: "POST",
    query: {
      monAnId,
      soLuong,
    },
  });
}

export function confirmOrder(reservationTableId: number): Promise<BanDat> {
  return apiRequest<BanDat>(`/api/orders/reservation-tables/${reservationTableId}/confirm`, {
    method: "POST",
  });
}
