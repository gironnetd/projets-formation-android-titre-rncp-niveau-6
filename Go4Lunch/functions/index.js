const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();
db.settings({ timestampsInSnapshots: true });

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.middayRestaurantCleanup = functions.pubsub.schedule('every day 00:00')
  .timeZone('Europe/Paris') // Users can choose timezone - default is America/Los_Angeles
  .onRun((context) => {
  console.log('Midday Restaurant cleanup every day at midnight from scheduledFunctionCrontab');
  const restaurants = findRestaurants();

  restaurants.then(restaurants => {
    restaurants.forEach(restaurant => {
      const restaurantBatch = admin.firestore().batch();
      console.log(restaurant);
        restaurantBatch.update(restaurant.ref, {
          workmates: null
      });
      
      const workmates = restaurant.ref.collection('workmates').get();
      workmates.then(workmates => {
          workmates.forEach(workmate => {
            const workmateBatch = admin.firestore().batch();
            workmateBatch.delete(workmate.ref);
            workmateBatch.commit();
            return null;
          })
          return null;
      }).catch(console.error);

      restaurantBatch.commit();
      return null;
    });
    return null;
  }).catch(console.error);
  
  const workmates = findWorkmates();

  workmates.then( workmates => {
      workmates.forEach(workmate => {
          const workmateBatch = admin.firestore().batch();
          workmateBatch.update(workmate.ref, {
            middayRestaurant: null,
            middayRestaurantId: null,
            middayRestaurantName: null
          });
          workmateBatch.commit();
          return null;
      });
      return null;
  }).catch(console.error);
  
  return null;
});

function findRestaurants() {
  return db.collection('restaurants').get();
}

function findWorkmates() {
  return db.collection('workmates').get();
}