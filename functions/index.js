'use strict'

const functions = require('firebase-functions');
var admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('messages/{user_id}/{notification_id}').onWrite((snapshot, context) => {
    const TAG = "sendNotification: ";

    const data = snapshot.after._data;
    console.log(TAG + "De: " + data.de + " Para: " + data.para);

    const msg = {
        notification: {
            title: data.title,
            body: data.body,
            android_channel_id: data.channel,
            click_action: data.action
        }
    }

    return admin.messaging().sendToDevice(data.token, msg);
});