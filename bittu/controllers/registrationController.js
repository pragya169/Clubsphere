const db = require("../config/db");

exports.registerParticipant = (req, res) => {
  const { name, enrollment, email, phone } = req.body;

  // Validate input
  if (!name || !enrollment || !email || !phone) {
    return res.status(400).json({ error: "All fields are required" });
  }

  const sql = "INSERT INTO registrations (name, enrollment, email, phone) VALUES (?, ?, ?, ?)";
  db.query(sql, [name, enrollment, email, phone], (err, result) => {
    if (err) {
      console.error("Error inserting registration:", err);
      return res.status(500).json({ error: "Failed to register participant" });
    }
    res.status(201).json({ message: "Registration successful", id: result.insertId });
  });
};
