/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationOnNewMessage = functions.firestore
    .document('chats/{chatId}/messages/{messageId}')
    .onCreate(async (snap, context) => {
        const message = snap.data();
        const chatId = context.params.chatId;

        // Get the FCM token of the receiver
        const userDoc = await admin.firestore().collection('users').doc(message.receiverId).get();
        const fcmToken = userDoc.data().fcmToken;

        if (fcmToken) {
            const payload = {
                notification: {
                    title: 'New Message',
                    body: message.content,
                },
                token: fcmToken,
            };

            try {
                await admin.messaging().send(payload);
                console.log('Notification sent successfully');
            } catch (error) {
                console.error('Error sending notification:', error);
            }
        }
    });