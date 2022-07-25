import express from "express";
import { Request, Response } from "express";
import { CreatePaymentRequest, Money } from "square";
import { UserOrder, PaymentBody } from "../types";

import { paymentsApi, ordersApi } from "../api/square";
import db from "../db";

const router = express.Router();

router.post("/", async (req: Request, res: Response) => {
  const {
    sourceId,
    idempotencyKey,
    amountMoney,
    orderId,
    userOrder,
  }: PaymentBody = req.body;
  const money: Money = { amount: BigInt(amountMoney), currency: "CAD" };
  console.log("money", money);

  const body: CreatePaymentRequest = {
    sourceId,
    orderId,
    idempotencyKey,
    amountMoney: money,
    autocomplete: false,
  };

  try {
    const { result, ...httpResponse } = await paymentsApi.createPayment(body);
    userOrder.paymentId = result.payment?.id;
    await db.order.addUserOrderToOrder(orderId, userOrder);

    // const response = await ordersApi.retrieveOrder(orderId);
    // const order = response.body;

    const userOrders: UserOrder[] = await db.order.getUserOrdersByOrderId(
      orderId
    );

    let total: bigint = BigInt(0);
    for (const userOrder of userOrders) {
      total += userOrder.amount?.amount || BigInt(0);
    }

    console.log("result.payment", result.payment);
    res.status(httpResponse.statusCode).send(result.payment);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

module.exports = router;
