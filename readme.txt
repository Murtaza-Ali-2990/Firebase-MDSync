Firebase Test App


The project deals with multi-device sync using Firebase Firestore and Firebase Cloud Messaging.

Features:
 - On Update Table which gets updated through Snapshot Listener when data has been modified in database.
 - Notification Table which gets updated through FCM received by service.

Workflow:
 - Realtime addition of data:

	1. Data is added through the AddUserData Activity.
		# Data is first sent to Notification Table, then to On Update table.
		# RowId from On Update table is added to SharedPreferences named 'id' in field 'id'.
		# RowId from Notification Table is added to the existing userData object.
		# userData object (with 'id' of data from Notification Table) is sent to Firebase with document name as rowId.

	2. Data added on Firestore in Firebase.
		# UserData is added to the desired path.

	3. In Main Activity/ On Update Table
		# Snapshot listener monitors any change in data on Firestore, gets triggered whenever there is a modification.
		# Snapshot Listener fetches all the data which is greater than the current stored 'id' in SharedPreferences (filters using isGreaterThan()).
		# First time app is opened, initial value of 'id' is 0. It gets updated to max value of id in Firestore.
		# When data having 'id' greater than currently stored 'id' in shared preferences is added to cloud, On Update table is updated, and data is added.
		# 'id' value in SharedPreferences is changed with every update in local database.

 - Addition/Modification of data through FCM
	* to update -> updates = 1
	  to add/create -> updates = 0

	1. Data is added through the AddUserData Activity.
		# Data is first sent to Notification Table, then to On Update table.
		# RowId from On Update table is added to SharedPreferences named 'id' in field 'id'.
		# RowId from Notification Table is added to the existing userData object.
		# userData object (with 'id' of data from Notification Table) is sent to Firebase with document name as rowId.

	2. Data added on Firestore in Firebase.
		# UserData is added to the desired path.
		# Addition of data triggers CreateDoc method of cloud functions.
		# CreateDoc method monitors addition of new data in Firestore using onCreate().
		# It fetches all the device tokens present in the token document and send data payload with high priority to all the device tokens.

	3. When multiple devices add data with same id.
	* This results in multiple tasks having same 'id'
	** Tasks have same id on different devices, not on cloud database. For example, say M1 has task Tk1 on id 6, and M2 has task Tk2 on id 6, when both device, or one of them is offline.
		# When two or more devices come online, the data added through second device overwrites the data of first device.
		# This triggers the UpdateDocs method present in cloud functions.
		# UpdateDoc method monitors modification of existing data in Firestore using onUpdate().
		# The function first checks the 'updates' parameter of updated data.
		# If value of 'updates' is 0:
			 - The data was meant to be added, from other device. The function adds the data present before the update, to the database, on a new id, that is 'max id in database' + 1.
			 - This addition of new data triggers CreateDoc method which sends data FCM to all devices as described above. Every device adds this new data.
		# The UpdateDoc method sends the payload of the updated UserData to every token present in document.

	4. FBMessageService Service recieves all FCMs through onMessageRecieved.
		# It first checks if the 'id' of the recieved payload is already present in the database.
		# If 'id' is present:
			 - Check the 'updates' field of the data. If the 'updates' is 1:
				 > The already present data has to be overridden by newly recieved data payload.
				 > Notification of updated data is displayed on device.
			 - If 'updates' is 0:
				 > Recieved data payload has to be ignored.
		# If 'id' is not present:
			 - Data from data payload has to be added into the database on the 'id' present in the payload.
			 - Notification of added data is displayed on device.

 - Addition of device tokens in Firestore

	1. When a user performs a login on a device.
		# The current device token is fetched from FirebaseInstanceId.
		# The existing tokens are fetched from the firestore, as a document, if any.
		# If there are no existing tokens in Firestore/size of fetched token document is 0:
			 - Current device token is added to an ArrayList, which in turn is added to a HashMap with key 'Token'.
			 - Resulting HashMap is added to Firestore as a document.
		# If token document exists/has size greater than 0:
			 - Fetched document gets array of tokens extracted from it (type casted to ArrayList<String>) and stored.
			 - Resulting array is checked, if the current device token is already present in it.
			 - If token is not present in ArrayList of tokens:
				 > current device token is appended to the ArrayList of tokens.
				 > The List is then 'put' in a HashMap with key 'Token'.
				 > Resulting HashMap is added to Firestore as a document.
			 - If token is already present in List of tokens, data is not modified in any way.
		# Current device token is stored in SharedPreferences 'token', with key 'token' for furthur use.

