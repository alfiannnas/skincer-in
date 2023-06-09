const express = require("express");
const User = require("../models/user");

const router = express.Router();
router.post("/register", async (req, res) => {
    try {
      const { fullName, email, password } = req.body;
  
      const alreadyExistsUser = await User.findOne({ where: { email } });
  
      if (alreadyExistsUser) {
        return res.json({ message: 'An existing email user' });
      }
  
      const newUser = new User({ fullName, email, password });
      const savedUser = await newUser.save();
  
      if (savedUser) {
        return res.json({ message: 'Thank you for signing up!' });
      }
    } catch (err) {
      console.log('Error: ', err);
      return res.json({ error: 'Unable to create user account' });
    }
});

module.exports = router;
