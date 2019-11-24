
import { Venue } from "./Venue";
import * as admin from 'firebase-admin';
admin.initializeApp();
const db = admin.firestore();


export const getDateToday = () => {
    let currentDate = new Date();
    currentDate.setDate(currentDate.getDate());
    let day = currentDate.getDate().toString();
    if (day.length == 1) day = "0" + day;
    let month = (currentDate.getMonth() + 1).toString();
    let year = currentDate.getFullYear().toString();
    return day + "-" + month + "-" + year;
}

export const getDateTomorrow = () => {
    let currentDate = new Date();
    currentDate.setDate(currentDate.getDate() + 1);
    let day = currentDate.getDate().toString();
    if (day.length == 1) day = "0" + day;
    let month = (currentDate.getMonth() + 1).toString();
    let year = currentDate.getFullYear().toString();
    return day + "-" + month + "-" + year;
}

export const getDateTheDayAfterTomorrow = () => {
    let currentDate = new Date();
    currentDate.setDate(currentDate.getDate() + 2);
    let day = currentDate.getDate().toString();
    if (day.length == 1) day = "0" + day;
    let month = (currentDate.getMonth() + 1).toString();
    let year = currentDate.getFullYear().toString();
    return day + "-" + month + "-" + year;
}


export const getDateInThreeDays = () => {
    let currentDate = new Date();
    currentDate.setDate(currentDate.getDate() + 3);
    let day = currentDate.getDate().toString();
    if (day.length == 1) day = "0" + day;
    let month = (currentDate.getMonth() + 1).toString();
    let year = currentDate.getFullYear().toString();
    return day + "-" + month + "-" + year;
}

export const addVenueToDate = async (venue: Venue, date: string, city: string) => {
    try {
        console.log("Started addVenueToDate");
        await db.collection(`${city}_dates`).doc(date).collection("day_venues").doc(venue.venueName)
            .set(JSON.parse(JSON.stringify(venue)));
        return null;
    } catch (error) {
        console.log(error);
        return null;
    }

}

export const setUpOneCityOneDate = async (city: string, date: string) => {
    try {
        console.log("Started set up one city date");
        const venueSnapshot = await db.collection("cities").doc(city).collection("venues").get()
        const promises: Array<Promise<null>> = [];
        venueSnapshot.forEach(doc => {
            const v = doc.data();
            const venue = new Venue(v.venueName, v.rating, v.address, v.location, v.openingHours, v.type, v.imageId);
            const p = addVenueToDate(venue, date, city);
            promises.push(p);
        })
        return Promise.all(promises);
    } catch (error) {
        console.log(error);
        const promises: Array<Promise<null>> = [];
        return Promise.all(promises);
    }
}


export const addDateDummy = async (city: string, date: string) => {
    try {
        console.log("Started addDateDummy");
        await db.collection(`${city}_dates`).doc(date)
            .set({
                exists: true
            });
        const promises: Array<Promise<null>> = [];
        return Promise.all(promises);
    } catch (error) {
        console.log(error);
        const promises: Array<Promise<null>> = [];
        return Promise.all(promises);
    }
}



//Runs everyday, sets up the day in three days
export const setUpNewDayInDB = async () => {
    try {
        console.log("Started setUpnewDay");
        const citiesSnapshot = await db.collection('cities').get();
        const promises: Array<Promise<null[]>> = []
        citiesSnapshot.forEach(city => {
            console.log("City = " + city.id);
            const d = addDateDummy(city.id, getDateInThreeDays());
            const p = setUpOneCityOneDate(city.id, getDateInThreeDays());
            promises.push(d);
            promises.push(p);
        })
        return Promise.all(promises)
    } catch (error) {
        console.log(error);
        return null;
    }
}

// Runs once, when app is initialized
export const setUpFourDaysInDB = async () => {
    try {
        console.log("Started setUpnewDay");
        const citiesSnapshot = await db.collection('cities').get();
        const promises: Array<Promise<null[]>> = []
        citiesSnapshot.forEach(city => {
            console.log("City = " + city.id);
            const d1 = addDateDummy(city.id, getDateToday());
            const d2 = addDateDummy(city.id, getDateTomorrow());
            const d3 = addDateDummy(city.id, getDateTheDayAfterTomorrow());
            const d4 = addDateDummy(city.id, getDateInThreeDays());

            const p1 = setUpOneCityOneDate(city.id, getDateToday());
            const p2 = setUpOneCityOneDate(city.id, getDateTomorrow());
            const p3 = setUpOneCityOneDate(city.id, getDateTheDayAfterTomorrow());
            const p4 = setUpOneCityOneDate(city.id, getDateInThreeDays());

            promises.push(d1);
            promises.push(d2);
            promises.push(d3);
            promises.push(d4);

            promises.push(p1);
            promises.push(p2);
            promises.push(p3);
            promises.push(p4);
        })
        return Promise.all(promises)
    } catch (error) {
        console.log(error);
        return null;
    }
}

export const setUpCityInDB = async () => {
    await  db.collection("cities").doc("Kaunas,LT").set({
        exists: true
    });
    return null; 
}






