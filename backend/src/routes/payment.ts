import express from "express";
import { Request, Response } from "express";
import { CreatePaymentRequest } from "square";

import { paymentsApi } from "../api/square";

const router = express.Router();

router.post("/", async (req: Request, res: Response) => {
    const { sourceId, idempotencyKey, amountMoney } = req.body;

    const body: CreatePaymentRequest = {
        sourceId,
        idempotencyKey,
        amountMoney,
        autocomplete: false,
    };

    try {
        const { result, ...httpResponse } = await paymentsApi.createPayment(body);
        res.status(httpResponse.statusCode).send(result.payment)

    } catch (error) {
        console.log(error);
        res.sendStatus(500);
    }
});

module.exports = router;
