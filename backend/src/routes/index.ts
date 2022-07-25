module.exports = (app: any) => {
  app.use("/order", require("./order"));
  app.use("/payment", require("./payment"));
  app.use("/user", require("./user"));
  return app;
};
