import express from "express";
import { Request, Response } from "express";
import {
  SearchOrdersStateFilter,
  SearchOrdersFilter,
  SearchOrdersQuery,
  SearchOrdersRequest,
  PayOrderRequest,
  Order,
} from "square";
import { v4 as uuidv4 } from "uuid";

import db from "../db";
import { OrderMeta, UserOrder } from "../types";

import { ordersApi } from "../api/square";
import { admin, messaging } from "../firebase";

const router = express.Router();
router.post("/example/table/:tableId", async (req: Request, res: Response) => {
  try {
    const { tableId } = req.params;
    const idempotencyKey = uuidv4();
    const { result, ...httpResponse } = await ordersApi.createOrder({
      order: {
        locationId: "L3GAERGV19EXB",
        lineItems: [
          {
            quantity: "1",
            catalogObjectId: "WCQXY6CAXNFLUZHTHER3MLFT",
            itemType: "ITEM",
          },
          {
            quantity: "3",
            catalogObjectId: "IYRQZRWFEPUPDZLGFXYHE7VQ",
            itemType: "ITEM",
          },
          {
            quantity: "1",
            catalogObjectId: "62AKEOB7I6ICWRWXB7GCHGLQ",
            itemType: "ITEM",
          },
        ],
        ticketName: tableId,
      },
      idempotencyKey,
    });

    const { statusCode, body } = httpResponse;
    res.status(statusCode).send(result.order);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

router.post("/:orderId/pay", async (req: Request, res: Response) => {
  const { orderId } = req.params;
  const { idempotencyKey, paymentIds } = req.body;

  console.log("== Pay Order");
  console.log("orderId", orderId);
  console.log("idempotencyKey", idempotencyKey);
  console.log("paymentIds", paymentIds);

  try {
    const paymentBody: PayOrderRequest = {
      idempotencyKey,
    };
    paymentBody.paymentIds = paymentIds;

    const { result, ...httpResponse } = await ordersApi.payOrder(orderId, paymentBody);
    const { statusCode, body } = httpResponse;

    res.status(statusCode).send(result.order);
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

    console.log("get bill");
    console.log("locationId", locationId);
    console.log("tableId", tableId);

    const bodyQueryFilter: SearchOrdersFilter = {};
    bodyQueryFilter.stateFilter = bodyQueryFilterStateFilter;

    const bodyQuery: SearchOrdersQuery = {};
    bodyQuery.filter = bodyQueryFilter;

    const body: SearchOrdersRequest = {};
    body.locationIds = [locationId];
    body.query = bodyQuery;

    const response = await ordersApi.searchOrders(body);
    const order = response.result.orders?.find((order) => order.ticketName === tableId);
    const userOrders = await db.order.getUserOrdersByOrderId(order?.id!);

    const orderData: OrderMeta = {
      order,
      userOrders,
    };

    res.status(200).send(orderData);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

router.post("/square/update", async (req: Request, res: Response) => {
  try {
    const { data } = req.body;
    const { type } = data;
    let orderId;
    console.log("==SQUARE UPDATE");
    console.log("data", data);

    if (type === "order") {
      orderId = data.id;
    } else if (type === "payment") {
      orderId = data.object.order_id;
    }

    const userOrders: UserOrder[] = await db.order.getUserOrdersByOrderId(orderId);
    const firebaseTokens: string[] = [];

    for (const userOrder of userOrders) {
      const user = await db.user.getUserById(userOrder.userId);
      console.log("user", user);
      if (user.firebaseMessagingToken) {
        firebaseTokens.push(user.firebaseMessagingToken);
      }
    }

    const message = {
      data: { refresh: "true" },
      tokens: firebaseTokens,
    };
    console.log("message", message);

    await admin.messaging().sendMulticast(message);

    res.sendStatus(200);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

module.exports = router;
