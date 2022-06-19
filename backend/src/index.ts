require("dotenv").config({
  path: `.env.${process.env.NODE_ENV}`,
});

(BigInt.prototype as any).toJSON = function () {
  return this.toString();
};

const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const mountRoutes = require("./routes");

const host = process.env.HOST;
const port = process.env.PORT;

(async () => {
  try {
    if (!host || !port) {
      throw "Missing environment variables";
    }

    const app = express();

    app.use(cors());
    app.options("*", cors());

    app.use(bodyParser.json()); // support json encoded bodies
    app.use(bodyParser.urlencoded({ extended: false })); // support encoded bodies
    // app.use("/v1.0", require("./routes/index")(app));
    mountRoutes(app);

    app.listen(port, () => console.log(`Backend listening at http://${host}:${port}`));
  } catch (e) {
    console.error(e);
  }
})();
