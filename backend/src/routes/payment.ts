import express from "express";
import { Request, Response } from "express";
import { SearchOrdersStateFilter, SearchOrdersFilter, SearchOrdersQuery, SearchOrdersRequest } from "square";

import { paymentsApi } from "../api/square";

const router = express.Router();

// router.put("/order/:orderId", async (req: Request, res: Response) => {

// } catch (error) {
//     console.log(error);
//     res.sendStatus(500);
// }

module.exports = router;
