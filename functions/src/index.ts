import * as functions from 'firebase-functions';
import { setUpNewDayInDB, setUpFourDaysInDB, setUpCityInDB } from './DatabaseMethods';
/* import * as admin from 'firebase-admin';


let serviceAccount = require('C:\\Users\\tstim\\Documents\\ErasmusParty\\ErasmusPartyApp\\key\\erasmuspartyapp-firebase-adminsdk-igx4l-ced97f48f2.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://erasmuspartyapp.firebaseio.com"
}); */

// Write actual scheduled function 

export const setUpANewDate = functions.pubsub
    .schedule('00 03 * * *')
    .timeZone('Europe/Vienna')
    .onRun(context => {
        return setUpNewDayInDB();
    })

export const setUpFourNewDates = functions.pubsub
    .schedule('25 00 4 11 *')
    .timeZone('Europe/Vilnius')
    .onRun(context => {
        return setUpFourDaysInDB();
    })

export const setUpACity = functions.pubsub
    .schedule('25 00 4 11 *')
    .timeZone('Europe/Vilnius')
    .onRun(context =>{ 
        return setUpCityInDB(); 
    })




