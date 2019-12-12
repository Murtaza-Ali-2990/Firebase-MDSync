const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

/*
exports.addMessage = functions.firestore
	.document('user/{userId}/names/{docId}').onCreate((data, context) => {
  var jsonString = data.data();
  var s = JSON.stringify(jsonString, null, 2);
  console.log(s, context.params.userId);
  return null;
});
*/

exports.CreateDoc = functions.firestore.document('user/{userId}/names/{docId}').onCreate((snap, context) => {
	const userId = context.params.userId;
	const docId = context.params.docId;

	const ref = snap.data();
	const tokenRef = admin.firestore().collection('user').doc(userId).collection('Token').doc('token');

	return tokenRef.get().then(snap => {
		var tokens = snap.data().Token;

		var message = {
			data: {
				id: ref.id.toString(),
				name: ref.name,
				surname: ref.surname,
				sex: ref.sex,
				age: ref.age.toString(),
				token: ref.token,
				updates: ref.updates.toString()
			},
			android: {
				priority: 'high'
			},
			tokens: tokens
		};

		return admin.messaging().sendMulticast(message).then(response => {
			console.log('Message sent successfully to ' + response.successCount + ' devices', response);
			return null;
		}).catch(error => {
			console.log('Error sending message', error);
		});
	}).catch(error => {
		console.log('Error fetching tokens', error);
	});
});

exports.UpdateDoc = functions.firestore.document('user/{userId}/names/{docId}').onUpdate((change, context) => {
	const userId = context.params.userId;
	const docId = context.params.docId;

	const afterRef = change.after.data();
	const beforeRef = change.before.data();

	const cRef = admin.firestore().collection('user').doc(userId).collection('names');
	const tokenRef = admin.firestore().collection('user').doc(userId).collection('Token').doc('token');

	if(afterRef.updates === 0) {
		cRef.orderBy('id', 'desc').limit(1).get().then(res => {
			res.forEach(function(snapshot) {
				let id = snapshot.data().id + 1;
				let udata = {
					id: id,
					name: beforeRef.name,
					surname: beforeRef.surname,
					sex: beforeRef.sex,
					age: beforeRef.age,
					token: beforeRef.token,
					updates: 0
				};

				return cRef.doc(id.toString()).set(udata).then(ref => {
					console.log('Doc added Successfully with ID: ' + id, ref);
					return null;
				}).catch(error => {
					console.log('Doc not added', error);
				});				
			});
			return null;
		}).catch(error => {
			console.log('Error fetching maximum id', error);
		});
	}

	return tokenRef.get().then(snap => {
		var tokens = snap.data().Token;

		var message = {
			data: {
				id: afterRef.id.toString(),
				name: afterRef.name,
				surname: afterRef.surname,
				sex: afterRef.sex,
				age: afterRef.age.toString(),
				token: afterRef.token,
				updates: '1'
			},
			android: {
				priority: 'high'
			},
			tokens: tokens
		};

		return admin.messaging().sendMulticast(message).then(response => {
			console.log('Update sent successfully to ' + response.successCount + ' devices', response);
			return null;
		}).catch(error => {
			console.log('Update not sent', error);
		});
	}).catch(error => {
		console.log('Error fetching tokens', error);
	});
});
