import { Money } from "square";

export interface OrderItem {
  item_id: String;
  amount: Money;
  quantity: Number;
}

export interface UserOrders {
  user_id: String;
  items: OrderItem[];
}
