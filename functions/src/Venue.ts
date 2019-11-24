export class Venue {

    venueName: string;
    rating: string;
    address: string;
    location: string;
    type:string;

    imageId: number;
    numberOfAttendees: number;

    openingHours: Array<string>;
    guestList: Array<string>;
    usersNationalitiesMap: Map<string,string>; 


    constructor(venueName:string, rating:string, address:string, location:string, openingHours:Array<string>, type:string, imageId:number){ 
        this.venueName = venueName; 
        this.rating = rating; 
        this.address = address;
        this.location = location;
        this.openingHours = openingHours;
        this.type = type;
        this.numberOfAttendees = 0; 
        this.openingHours = openingHours; 
        this.guestList = new Array<string>(); 
        this.imageId = imageId; 
        this.usersNationalitiesMap = new Map<string,string>(); 
    }
}


