import express from "express";
import { Request, Response } from "express";
import { User } from "../types";

import db from "../db";

const router = express.Router();

router.post("/:userId/update-firebase-token", async (req: Request, res: Response) => {
  const { userId } = req.params;
  const { token } = req.body;
  console.log("==UPDATE FIREBASE TOKEN");
  console.log("userId", userId);
  console.log("token", token);

  if (!userId || !token) {
    res.sendStatus(400);
  }

  try {
    await db.user.updateFirebaseMessagingToken(userId, token);

    res.sendStatus(200);
  } catch (error) {
    console.log(error);
    res.sendStatus(500);
  }
});

module.exports = router;
