const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();


exports.addMessage = functions.firestore
	.document('user/{userId}/names/{docId}').onCreate((data, context) => {
  console.log('this is wokin');
  const k = data.data();
  console.log('this is in ' + data.data().Gender);
  var jsonString = data.data();
  var s = JSON.stringify(jsonString, null, 2);
  console.log(s);
  console.log(context.params.userId);
  
});

exports.sendMsg = functions.firestore
	.document('user/{userId}/names/{docId}').onWrite((change, context) => {

		const userId = context.params.userId;
		const docId = context.params.docId;

var title = change.after.data().Name;
var rectoken = change.after.data().Token;

var time = change.after.createTime.toDate().toLocaleString("en-US", {timeZone: "Asia/Kolkata"});
time = new Date(time);

console.log(time, change);


var docRef = admin.firestore().collection('user').doc(userId).collection('Token').doc('token');
return docRef.get().then(snap =>{
	var t = snap.data().Token;
	console.log(t, snap.data());

	t.forEach(function(token){
		console.log('token', token);
		if(rectoken === token)
			return null;

		var message = {
  	notification: {
    	title: title,
    	body: 'Hey...here I am'
  	},
  	token: token,
  	data: {
  		id: '1004',
  		city: 'Indore',
  		message: 'yolo'
  	}
	};
		return admin.messaging().send(message)
  		.then((response) => {
    	// Response is a message ID string.
    		console.log('Successfully sent message:', response);
    		return null;
  		})
  		.catch((error) => {
    		console.log('Error sending message:', error);
  		});
	});
	return null;
	}).catch(err =>{
		console.log("Error", err);
	});
});
