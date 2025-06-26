import express from "express";
import dotenv from "dotenv";
import chatProxy from "./chatProxy.js";

dotenv.config();
const app = express();
app.use(express.json());

// Add chat proxy route
app.use("/api", chatProxy);

// (Optional) Add a health check route
app.get("/api/health", (req, res) => res.send("OK"));

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
