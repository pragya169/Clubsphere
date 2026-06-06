const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");
const dotenv = require("dotenv");

dotenv.config();
const app = express();
const PORT = process.env.PORT;

const corsOptions = {
  origin: "http://127.0.0.1:5500", // Your frontend's origin
  methods: ["POST", "GET"],
  credentials: true,
};
app.use(cors(corsOptions));

// ✅ Middleware
app.use(cors());
app.use(bodyParser.json());

// Routes
const authRoutes = require("./routes/authRoutes");
app.use("/api/auth", authRoutes);
const registrationRoutes = require("./routes/registrationRoutes");
app.use("/api/registration", registrationRoutes);

app.get("/", (req, res) => {
  res.send("Server is working!");
});


app.listen(PORT, () => {
  console.log(`🚀 Server running on port ${PORT}`);
});
