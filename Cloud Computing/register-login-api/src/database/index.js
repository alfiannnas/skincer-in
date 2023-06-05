const { Sequelize } = require("sequelize");

const dbName = process.env.SQL_DATABASE;
const dbUser = process.env.SQL_USER;
const dbPassword = process.env.SQL_PASSWORD;
const dbConnectionName = process.env.SQL_CONNECTION_NAME;

const sequelize = new Sequelize(dbName, dbUser, dbPassword, {
    dialect: 'mysql',
    dialectOptions: {
      socketPath: `/cloudsql/${dbConnectionName}`,
    },
});

sequelize.sync();

(async () => {
  try {
    await sequelize.authenticate();
    console.log("Connection has been established successfully.");
  } catch (error) {
    console.error("Unable to connect to the database:", error);
  }
})();

module.exports = sequelize;
