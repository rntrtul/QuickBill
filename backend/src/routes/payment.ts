import express from "express";
import { Request, Response } from "express";
import { CreatePaymentRequest, Money, PayOrderRequest } from "square";
import { UserOrder, PaymentBody } from "../types";
import { v4 as uuidv4 } from "uuid";

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

    const response = await ordersApi.retrieveOrder(orderId);
    const { order } = response.result;

    const userOrders: UserOrder[] = await db.order.getUserOrdersByOrderId(
      orderId
    );

    let total: bigint = BigInt(0);
    const paymentIds: string[] = [];
    for (const userOrder of userOrders) {
      total += userOrder.amount?.amount || BigInt(0);
    }

    if (order?.totalMoney?.amount && total >= order?.totalMoney?.amount) {
      const idempotencyKey = uuidv4();
      const paymentBody: PayOrderRequest = {
        idempotencyKey,
      };
      paymentBody.paymentIds = paymentIds;

      const { result, ...httpResponse } = await ordersApi.payOrder(
        orderId,
        paymentBody
      );
    }

    console.log("result.payment", result.payment);
    res.status(httpResponse.statusCode).send(result.payment);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

module.exports = router;
