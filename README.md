# volcano-booking

## IntelliJ Setup
1. Install lombok plugin and have it enabled

## Running Application
### IntelliJ
run `VolcanoBookingSystemRunner.main`
### Gradle
execute `.gradlew bootrun' in the project directory

# Endpoints
The application runs on http://localhost:8080/

### `GET /availabilities`
Retrieves the date available for booking.

Body
```  
{  
 "from": "2020-12-18", 
 "to": "2021-02-28"
}
```
|field|description|
|-----|-----------|
|from | date field format YYYY-MM-DD|
|to   | date field format YYYY-MM-DD|

Response 200
```  
[  
 { "date": "2020-12-18" }, 
 { "date": "2020-12-19" }, 
 { "date": "2020-12-20" }, 
 { "date": "2020-12-21" }, 
 { "date": "2020-12-22" }
]  
```

Response 400
```
{
  "status": "BAD_REQUEST",
  "timestamp": "2020-12-23 11:41:38",
  "errors": [
    {
      "field": "from",
      "message": "This date cannot be parsed. Please use date format: YYYY-MM-DD"
    }
  ]
}
```

### `GET /booking`
Retrieves the booking details

Body
```  
{  
 "ids": [1,2]
}
```
```  
{  
 "userId": 1
}
```
|field   |description|
|--------|-----------|
|ids     | arrays of ids of booking|
|userId  | user Id|

Response 200
```
[
  {
    "id": 1,
    "userId": 1,
    "checkInDate": "2020-12-21",
    "checkOutDate": "2020-12-24"
  },
  {
    "id": 2,
    "userId": 1,
    "checkInDate": "2020-12-26",
    "checkOutDate": "2020-12-28"
  }
]
```

### `POST /book`
Create booking

Body
```  
{
 "email":"john.wick@constantine.com",
 "firstName":"John",
 "lastName":"Constantine",
 "from":"2021-07-01",
 "to":"2021-07-02"
}
```
|field   |description|
|--------|-----------|
|email     | email address|
|firstName  | first name|
|lastName  | last name|
|from      | check-in date|
|to        | check-out date |

Constraints:
* Check-in date cannot be on booking date
* Booking duration cannot be greater than 3 days
* Booking can only be done up to 1 month in advance

Response 200
```
{
  "user": {
    "id": 4,
    "email": "john.wick@constantine.com",
    "firstName": "John",
    "lastName": "Constantine"
  },
  "booking": {
    "id": 5,
    "userId": 4,
    "checkInDate": "2021-01-01",
    "checkOutDate": "2021-01-02"
  }
}
```

Response 400
```
{
  "status": "BAD_REQUEST",
  "timestamp": "2020-12-23 12:24:40",
  "errors": [
    {
      "field": "from/to",
      "message": "The booking duration cannot be longer than 3 days."
    }
  ]
}
```

### `DELETE /book`
Delete booking

Body
```  
{
 "id":5
}
```
|field   |description|
|--------|-----------|
|id     | booking id|

Constraints:
*Booking Id needs to exist

Response 200
```
{
  "booking": {
    "id": 5,
    "userId": 4,
    "checkInDate": "2021-01-01",
    "checkOutDate": "2021-01-02"
  }
}
```

Response 400
```
{
  "status": "BAD_REQUEST",
  "timestamp": "2020-12-23 12:44:49",
  "errors": [
    {
      "field": "id",
      "message": "Booking Id does not exist."
    }
  ]
}
```