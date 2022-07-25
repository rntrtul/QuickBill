import { Money, Order } from "square";

export interface OrderItem {
  itemId: string;
  amount: Money;
  quantity: number;
}

export interface UserOrder {
  userId: string;
  items?: OrderItem[];
  amount?: Money;
  paymentId?: string;
}

export interface FirebaseOrder {
  userOrders?: UserOrder[];
  userIds?: string[];
}

export interface OrderMeta {
  order?: Order;
  userOrders?: UserOrder[];
}

export interface PaymentBody {
  sourceId: string;
  orderId: string;
  idempotencyKey: string;
  amountMoney: number;
  userOrder: UserOrder;
}

export interface User {
  firebaseMessagingToken: string;
}
