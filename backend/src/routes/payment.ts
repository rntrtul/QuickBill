import express from "express";
import { Request, Response } from "express";
import { CreatePaymentRequest, Money } from "square";

import { paymentsApi } from "../api/square";

const router = express.Router();

router.post("/", async (req: Request, res: Response) => {
  const { sourceId, idempotencyKey, amountMoney } = req.body;
  const money: Money = { amount: BigInt(amountMoney) };

  const body: CreatePaymentRequest = {
    sourceId,
    idempotencyKey,
    amountMoney: money,
    autocomplete: false,
  };

  try {
    const { result, ...httpResponse } = await paymentsApi.createPayment(body);
    res.status(httpResponse.statusCode).send(result.payment);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

module.exports = router;
