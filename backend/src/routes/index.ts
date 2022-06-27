module.exports = (app: any) => {
  app.use("/order", require("./order"));
  return app;
};
