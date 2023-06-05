'use strict';

/** @type {import('sequelize-cli').Migration} */
module.exports = {
  async up (queryInterface, Sequelize) {
    return queryInterface.bulkInsert('Users', [{
        fullName: 'pandji2',
        email: 'example2@example.com',
        password:'12345678',
        createdAt: new Date(),
        updatedAt: new Date()
      }]);
  },

  async down (queryInterface, Sequelize) {
    return queryInterface.bulkDelete('Users', null, {});
  }
};
