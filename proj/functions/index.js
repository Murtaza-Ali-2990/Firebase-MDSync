const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();


exports.addMessage = functions.firestore
	.document('user/{userId}/names/{docId}').onCreate((data, context) => {
  var jsonString = data.data();
  var s = JSON.stringify(jsonString, null, 2);
  console.log(s, context.params.userId);
  return null;
});

/*

exports.sendMsg = functions.firestore
	.document('user/{userId}/names/{docId}').onWrite((change, context) => {

		const userId = context.params.userId;
		const docId = context.params.docId;

var title = change.after.data().name;
var rectoken = change.after.data().token;

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
*/

exports.notifUpdate = functions.firestore.document('user/{userId}/names/{docId}').onWrite((change, context) => {
	const userId = context.params.userId;
	const docId = context.params.docId;

	const ref = change.after.data();
	var name = ref.name;
	var docToken = ref.token;

	const docRef = admin.firestore().collection('user').doc(userId).collection('Token').doc('token');

	return docRef.get().then(snap => {
		var tokens = snap.data().Token;

		tokens.forEach(function(token) {
			if(token === docToken)
				return null;
			console.log("Message will be delivered to " + token);

			var message = {
				notification: {
					title: 'Data has been added',
					body: name
				},
				data: {
					id: ref.id.toString(),
					name: ref.name,
					surname: ref.surname,
					sex: ref.sex,
					age: ref.age.toString()
				},
				token: token
			};

			return admin.messaging().send(message).then(response => {
				console.log('Message sent Successfully to ' + token, response);
				return null;
			}).catch(error => {
				console.log('Error sending message to ' + token, error);
			});
		});
		return null;
	}).catch(error => {
		console.log('Error fetching tokens', error);
	});
});