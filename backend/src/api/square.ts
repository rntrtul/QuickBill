import { Client, Environment } from "square";

const client = new Client({
  environment: Environment.Sandbox,
  accessToken: process.env.SQUARE_ACCESS_TOKEN,
});

const { ordersApi, paymentsApi } = client;

export { ordersApi, paymentsApi };
