# Notes on my thought process and decisions

## General 

- I decided to spend some time making the code follow the MVC pattern
- I decided to time-box the whole exercise to one full day of work
- My knowledge of Spring Boot is restricted to the coding assignments of the last couple of months, so for sure there will be mistakes along the way

## Redis

- I decided to use Redis instead of in-memory caching because I am a fan of using tools that have been developed specifically for a certain use case
- This was my first time interacting with Redis and its Spring Boot client
- The Redis configuration is basic, with more time it could be possible to investigate more precise configurations
- As a first, I'm not sure stringifying the list of Journeys per user is the best approach
- I tried to investigate using hash and list operations, but it was becoming too complex to solve withing the time box

## Endpoints

- The assignment instructions were contradictory: the user ID is required as a request header first, and then as a path parameter later. I decided to go for the request header option because I had never tried it in Spring Boot before, so I learned how to use it
- Extra time could be dedicated to handling all possible errors coming from Redis, and remapping them to the appropriate error types
- A balance needs to be found between how detailed the error codes/messages should be, and how much to disclose about the underlying backend structure
- I tend to choose a middle ground, where the status codes are descriptive, but the error messages are quite generic
- Not sure about returning 404 in case the user/journey ID doesn't exist, it might disclose too much

## Tests

- I tried to give an overview on how I do endpoint testing, though these are not exhaustive
- I prioritized endpoint tests over unit tests because I think it's a good way of spotting mistakes/inconsistencies in the full endpoint flow, without unit testing in detail but also being able to catch the main issues in the implementation
- With more time, it could be useful to test the single function behaviour in the service layer, so that all Redis errors are being caught, remapped and tested

## Assignment instructions

- I haven't used coroutines because I am not super confident with them, and it would have required extra investigation
- I am not sure whether stringifying affects the speed of data retrieval, but probably Redis is built for storing a list of custom objects on the same key, so if I had to guess, the Redis built-in data structures are optimized for speed
- The data retrieval speed has not been tested because it would have required to add much more data to Redis, and I was approaching my time constraints, so I preferred to spend the remaining time writing this file