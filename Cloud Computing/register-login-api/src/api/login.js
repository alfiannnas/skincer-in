const express = require("express");
const User = require("../models/user");
const jwt = require("jsonwebtoken");
const bcrypt = require("bcrypt");

const router = express.Router();

router.post("/login", async (req, res) => {
  const { email, password } = req.body;

  const userWithEmail = await User.findOne({ where: { email } }).catch(
    (err) => {
      console.log("Error: ", err);
    }
  );

  if (!userWithEmail || !(await bcrypt.compare(password, userWithEmail.password))) {
    return res.json({ message: "Incorrect email or password" });
  }

  const jwtToken = jwt.sign(
    { id: userWithEmail.id, email: userWithEmail.email },
    process.env.JWT_SECRET
  );

  res.json({ message: "Welcome Back!", username: { fullName: userWithEmail.fullName }, token: jwtToken });
});

module.exports = router;
