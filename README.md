# Venue Ticket Reservation Service

## Introduction 
This is my submission. A brief summary of the algorithm is provided. Some thoughts on the implemention are also provided. Finally, notes on how to build and run the program is shown below.

## Algorithm
Each seat in the venue is assigned a positive integer to represent how 'good' the seat is. The larger this number is, the better the seat in the venue is. The default best row is 2/3rds from the stage (i.e. if venue has nine rows, then the sixth row is the best row). The center seat in a row is the best, and the value in a row is decreasing on both sides from this center to the corner. An example of allocation of values to the seats is shown below for a 9x33 venue:

```
------------------------------------------------------   Stage    ---------------------------------------------------------------

1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  16  15  14  13  12  11  10   9   8   7   6   5   4   3   2   1 
2   4   6   8  10  12  14  16  18  20  22  24  26  28  30  32  34  32  30  28  26  24  22  20  18  16  14  12  10   8   6   4   2 
3   6   9  12  15  18  21  24  27  30  33  36  39  42  45  48  51  48  45  42  39  36  33  30  27  24  21  18  15  12   9   6   3 
4   8  12  16  20  24  28  32  36  40  44  48  52  56  60  64  68  64  60  56  52  48  44  40  36  32  28  24  20  16  12   8   4 
5  10  15  20  25  30  35  40  45  50  55  60  65  70  75  80  85  80  75  70  65  60  55  50  45  40  35  30  25  20  15  10   5 
6  12  18  24  30  36  42  48  54  60  66  72  78  84  90  96 102  96  90  84  78  72  66  60  54  48  42  36  30  24  18  12   6 
5  10  15  20  25  30  35  40  45  50  55  60  65  70  75  80  85  80  75  70  65  60  55  50  45  40  35  30  25  20  15  10   5 
4   8  12  16  20  24  28  32  36  40  44  48  52  56  60  64  68  64  60  56  52  48  44  40  36  32  28  24  20  16  12   8   4 
3   6   9  12  15  18  21  24  27  30  33  36  39  42  45  48  51  48  45  42  39  36  33  30  27  24  21  18  15  12   9   6   3 
```

To reserved _K_ number of seats, the current available best _K_ seats are determined with the following criteria: 
1. The system will try to pick physically adjacent seats in a row if possible, if a row does not fit, the adjacent row on the same side is selected.
2. The system will select a cluster of seats with least breaks between seats for the group of people.
3. When the multiple clusters of seats having the same number of breaks among seats are available, the cluster of seats with the highest sum of seat' values is picked. 
4. To ensure this mechanism, seats are selected in the order as such:
```
-->-->-->-->|
|<--<--<--<--
-->-->-->-->|
|<--<--<--<--
```

## Several Thoughts on Implementation
1. The intention was to write as little code as possible, so that it is easier and takes less time to review.
2. SeatHold class is intentionally designed as such, with only necessary information about the hold. An interface could have been provided here. But as discussed in 1, this is enough. The less attributes the user can manipulate, the better. Technically, I should have cloned the SeatHold object and returned the clone to the user such that the user cannot modify the data in the venue system.
3. Assumption in the code is that a user with the same email address will not hold seats more than once. Exisiting holds will be released, if the same email is used to hold seats again (even if the session time for previous hold did not expire.) This is to avoid availibility issues, where one user holds a lot of seats while trying to find a seat allocation the user likes affecting other users.
4. I did not raise exceptions in some exceptional cases as the interface did not support exceptions.
5. Validation of email addresses is not performed.


## Building and Testing
1. To build the project: ```gradle build```
2. To run the project (the main driver): ```gradle run```
3. To run the junit tests: ```gradle clean test```
4. To generate documentation: ```gradle javadoc```





