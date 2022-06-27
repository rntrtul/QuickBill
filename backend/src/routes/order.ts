import express from "express";
import { Request, Response } from "express";
import {
  SearchOrdersStateFilter,
  SearchOrdersFilter,
  SearchOrdersQuery,
  SearchOrdersRequest,
  PayOrderRequest,
} from "square";

import { ordersApi } from "../api/square";

const router = express.Router();

router.post("/:orderId/pay", async (req: Request, res: Response) => {
  const { orderId } = req.params;
  const { idempotencyKey, paymentIds } = req.body;
  try {
    const paymentBody: PayOrderRequest = {
      idempotencyKey,
    };
    paymentBody.paymentIds = paymentIds;

    const { result, ...httpResponse } = await ordersApi.payOrder(orderId, paymentBody);
    const { statusCode, body } = httpResponse;

    res.status(statusCode).send(body);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

router.get("/location/:locationId/table/:tableId", async (req: Request, res: Response) => {
  const { locationId, tableId } = req.params;
  try {
    const bodyQueryFilterStateFilterStates: string[] = ["OPEN"];
    const bodyQueryFilterStateFilter: SearchOrdersStateFilter = {
      states: bodyQueryFilterStateFilterStates,
    };

    const bodyQueryFilter: SearchOrdersFilter = {};
    bodyQueryFilter.stateFilter = bodyQueryFilterStateFilter;

    const bodyQuery: SearchOrdersQuery = {};
    bodyQuery.filter = bodyQueryFilter;

    const body: SearchOrdersRequest = {};
    body.locationIds = [locationId];
    body.query = bodyQuery;

    const response = await ordersApi.searchOrders(body);
    const order = response.result.orders?.find((order) => order.ticketName === tableId);

    res.status(200).send(order);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

module.exports = router;
